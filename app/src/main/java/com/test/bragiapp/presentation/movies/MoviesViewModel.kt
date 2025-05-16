package com.test.bragiapp.presentation.movies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.bragiapp.domain.interactor.GetMoviesUseCase
import com.test.bragiapp.domain.model.Movie
import com.test.bragiapp.presentation.common.UiState
import com.test.bragiapp.presentation.navigation.ARG_GENRE_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _moviesUiState = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val moviesUiState: StateFlow<UiState<List<Movie>>> = _moviesUiState.asStateFlow()


    val selectedGenreIdFromFilter: StateFlow<Int> = savedStateHandle.getStateFlow(ARG_GENRE_ID, -1)

    private var lastLoadedGenreId: Int? = null

    fun loadMovies(genreId: Int?) {
        lastLoadedGenreId = genreId
        viewModelScope.launch {
            getMoviesUseCase(genreId)
                .onStart { _moviesUiState.value = UiState.Loading }
                .catch { e ->
                    Timber.e(e, "Error loding movies for genreId: $genreId")
                    _moviesUiState.value = UiState.Error(parseErrorMessage(e))
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { movies ->
                            _moviesUiState.value = UiState.Success(movies)
                        },
                        onFailure = { e ->
                            Timber.e(e, "Failure loading movies for genreId: $genreId")
                            _moviesUiState.value = UiState.Error(parseErrorMessage(e))
                        }
                    )
                }
        }
    }


    fun updateSelectedGenreFilter(genreId: Int) {
        savedStateHandle[ARG_GENRE_ID] = genreId

    }

    fun retryLoadMovies() {
        loadMovies(lastLoadedGenreId)
    }

    private fun parseErrorMessage(e: Throwable): String {
        return when (e) {
            is java.net.UnknownHostException, is java.net.ConnectException ->
                "No connection. Please check your network and retry."

            else -> e.localizedMessage ?: "Error while fetching movies."
        }
    }
}