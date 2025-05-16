package com.test.bragiapp.di

import com.test.bragiapp.domain.interactor.GetGenresUseCase
import com.test.bragiapp.domain.interactor.GetMoviesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetMoviesUseCase(get()) }
    factory { GetGenresUseCase(get()) }
}