package com.test.bragiapp.presentation.filters


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.test.bragiapp.domain.model.Genre
import com.test.bragiapp.presentation.common.AppError
import com.test.bragiapp.presentation.common.LoadingIndicator
import com.test.bragiapp.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    navController: NavController,
    viewModel: FiltersViewModel,
    currentlyAppliedGenreId: Int,
    onApplyFilter: (Genre?) -> Unit
) {
    val genresState by viewModel.genresUiState.collectAsState()
    val locallySelectedGenre by viewModel.selectedGenre.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter by Genre") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = genresState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Success -> {
                    val allDisplayGenres = listOf(Genre(id = -1, name = "All")) + state.data

                    LaunchedEffect(state.data, currentlyAppliedGenreId) {
                        viewModel.setInitialSelection(state.data, currentlyAppliedGenreId)
                    }

                    FilterList(
                        genres = allDisplayGenres,
                        selectedGenre = locallySelectedGenre ?: Genre(id = -1, name = "All"),
                        onGenreClicked = { genre ->
                            val genreToSelect = if (genre.id == -1) null else genre
                            viewModel.selectGenre(genreToSelect)
                            onApplyFilter(genreToSelect)
                        }
                    )
                }
                is UiState.Error -> AppError(message = state.message) {
                    viewModel.loadGenres()
                }
            }
        }
    }
}

@Composable
fun FilterList(
    genres: List<Genre>,
    selectedGenre: Genre,
    onGenreClicked: (Genre) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(genres, key = { it.id }) { genre ->
            GenreChip(
                genre = genre,
                isSelected = genre.id == selectedGenre.id,
                onClick = { onGenreClicked(genre) }
            )
        }
    }
}

@Composable
fun GenreChip(
    genre: Genre,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(genre.name) },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = 4.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}