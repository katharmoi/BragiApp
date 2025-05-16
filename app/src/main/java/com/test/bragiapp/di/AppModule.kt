package com.test.bragiapp.di

import com.test.bragiapp.BuildConfig
import com.test.bragiapp.util.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(Constants.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor(maxRetries = Constants.MAX_RETRIES))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(Constants.TMDB_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0

        while (tryCount < maxRetries) {
            try {
                response?.close()
                Timber.d("RetryInterceptor: Attempting request (try $tryCount): ${request.url}")
                response = chain.proceed(request)
                if (response.isSuccessful) {
                    Timber.d("RetryInterceptor: Request successful (try $tryCount): ${request.url}")
                    return response
                } else {
                    Timber.w("RetryInterceptor: Request not successful (try $tryCount), code: ${response.code}, url: ${request.url}")
                }
            } catch (e: IOException) {
                exception = e
                Timber.e(e, "RetryInterceptor: IOException (try $tryCount): ${request.url}")
            }
            tryCount++
            if (tryCount >= maxRetries) {
                Timber.e("RetryInterceptor: Max retries reached for ${request.url}")
                break
            }

        }

        return response ?: throw (exception ?: IOException("Retry mechanism failed after $maxRetries attempts for ${request.url}"))
    }
}