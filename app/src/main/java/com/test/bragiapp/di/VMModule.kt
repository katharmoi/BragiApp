package com.test.bragiapp.di

import com.test.bragiapp.presentation.filters.FiltersViewModel
import com.test.bragiapp.presentation.movies.MoviesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MoviesViewModel(get(), get()) }
    viewModel { FiltersViewModel(get()) }
}