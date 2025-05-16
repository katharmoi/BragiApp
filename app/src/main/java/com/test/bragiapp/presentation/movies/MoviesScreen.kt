package com.test.bragiapp.presentation.movies

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.test.bragiapp.R
import com.test.bragiapp.domain.model.Movie
import com.test.bragiapp.presentation.common.AppError
import com.test.bragiapp.presentation.common.LoadingIndicator
import com.test.bragiapp.presentation.common.UiState
import com.test.bragiapp.presentation.navigation.Screen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    navController: NavController,
    viewModel: MoviesViewModel
) {
    val moviesState by viewModel.moviesUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bragi Movies") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.FiltersScreen.route)

            }) {
                Icon(Icons.Filled.Edit, "Filter movies")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues).
            fillMaxSize())
        {
            when (val state = moviesState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No movies found for this filter.")
                        }
                    } else {
                        MovieListGrid(movies = state.data)
                    }
                }
                is UiState.Error -> AppError(message = state.message) {
                    viewModel.retryLoadMovies()
                }
            }
        }
    }
}

@Composable
fun MovieListGrid(movies: List<Movie>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieItem(movie = movie)
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = movie.posterUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.movie_placeholder)
                        .error(R.drawable.movie_placeholder)
                        .build()
                ),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = movie.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text("Rating: ${"%.1f".format(movie.rating)}", fontSize = 12.sp)
                movie.budget?.takeIf { it > 0 }?.let {
                    Text(
                        "Budget: ${currencyFormat.format(it)}",
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                movie.revenue?.takeIf { it > 0 }?.let {
                    Text(
                        "Revenue: ${currencyFormat.format(it)}",
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}