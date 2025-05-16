package com.test.bragiapp.presentation.navigation

sealed class Screen(val route: String) {
    data object MoviesScreen : Screen("movies_screen")
}

const val ARG_GENRE_ID = "genreId"
