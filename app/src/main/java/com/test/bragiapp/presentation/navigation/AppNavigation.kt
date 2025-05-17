package com.test.bragiapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val mainGraphRoute = "main_navigation_graph"
    NavHost(
        navController = navController,
        startDestination = Screen.MoviesScreen.route + "?$ARG_GENRE_ID={$ARG_GENRE_ID}",
        route = mainGraphRoute
    ) {
        composable(
            route = Screen.MoviesScreen.route + "?$ARG_GENRE_ID={$ARG_GENRE_ID}",
            arguments = listOf(navArgument(ARG_GENRE_ID) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val mainGraphBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(mainGraphRoute)
            }
            val moviesViewModel: MoviesViewModel = koinViewModel(
                viewModelStoreOwner = mainGraphBackStackEntry
            )

            val initialGenreIdArg = backStackEntry.arguments?.getInt(ARG_GENRE_ID)

            val selectedGenreIdFromFilter by moviesViewModel.selectedGenreIdFromFilter.collectAsStateWithLifecycle()

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

        composable(Screen.FiltersScreen.route) { backStackEntry ->
            val filtersViewModel: FiltersViewModel = koinViewModel()

            val mainGraphBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(mainGraphRoute)
            }
            val moviesViewModel: MoviesViewModel =
                koinViewModel(viewModelStoreOwner = mainGraphBackStackEntry)

            val currentlyAppliedGenreId by moviesViewModel.selectedGenreIdFromFilter.collectAsStateWithLifecycle()


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