package com.test.bragiapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.test.bragiapp.presentation.filters.FiltersScreen
import com.test.bragiapp.presentation.filters.FiltersViewModel
import com.test.bragiapp.presentation.movies.MoviesScreen
import com.test.bragiapp.presentation.movies.MoviesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MoviesScreen.route + "?$ARG_GENRE_ID={$ARG_GENRE_ID}"
    ) {
        composable(
            route = Screen.MoviesScreen.route + "?$ARG_GENRE_ID={$ARG_GENRE_ID}",
            arguments = listOf(navArgument(ARG_GENRE_ID) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val moviesViewModel: MoviesViewModel = koinViewModel()

            val initialGenreIdArg = backStackEntry.arguments?.getInt(ARG_GENRE_ID)

            val selectedGenreIdFromFilter by moviesViewModel.selectedGenreIdFromFilter.collectAsState()

            LaunchedEffect(initialGenreIdArg, selectedGenreIdFromFilter) {
                val genreToLoad = if (selectedGenreIdFromFilter != -1) {
                    selectedGenreIdFromFilter
                } else if (initialGenreIdArg != -1) {
                    initialGenreIdArg
                } else {
                    null
                }
                moviesViewModel.loadMovies(genreToLoad)
            }

            MoviesScreen(
                navController = navController,
                viewModel = moviesViewModel
            )
        }

        composable(Screen.FiltersScreen.route) {
            val filtersViewModel: FiltersViewModel = koinViewModel()
            val moviesViewModel: MoviesViewModel = koinViewModel()
            val currentlyAppliedGenreId by moviesViewModel.selectedGenreIdFromFilter.collectAsState()


            FiltersScreen(
                navController = navController,
                viewModel = filtersViewModel,
                currentlyAppliedGenreId = currentlyAppliedGenreId,
                onApplyFilter = { selectedGenre ->
                    moviesViewModel.updateSelectedGenreFilter(selectedGenre?.id ?: -1)
                    navController.popBackStack()
                }
            )
        }
    }
}