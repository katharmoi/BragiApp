package com.test.bragiapp.di

import com.test.bragiapp.data.repository.MovieRepositoryImpl
import com.test.bragiapp.domain.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
}