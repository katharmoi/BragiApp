package com.test.bragiapp.presentation.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.bragiapp.domain.interactor.GetGenresUseCase
import com.test.bragiapp.domain.model.Genre
import com.test.bragiapp.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class FiltersViewModel(
    private val getGenresUseCase: GetGenresUseCase
) : ViewModel() {

    private val _genresUiState = MutableStateFlow<UiState<List<Genre>>>(UiState.Loading)
    val genresUiState: StateFlow<UiState<List<Genre>>> = _genresUiState.asStateFlow()


    private val _selectedGenre = MutableStateFlow<Genre?>(null) //null for all movies??TODO check logic
    val selectedGenre: StateFlow<Genre?> = _selectedGenre.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            getGenresUseCase()
                .onStart { _genresUiState.value = UiState.Loading }
                .catch { e ->
                    Timber.e(e, "Error loading genres")
                    _genresUiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { genres ->
                            _genresUiState.value = UiState.Success(genres)
                        },
                        onFailure = { e ->
                            Timber.e(e, "Failure loadiing genres")
                            _genresUiState.value = UiState.Error(e.message ?: "Error occurred fetching genres.")
                        }
                    )
                }
        }
    }


    fun selectGenre(genre: Genre?) {
        _selectedGenre.value = genre
    }


    fun setInitialSelection(genres: List<Genre>, appliedGenreId: Int) {
        _selectedGenre.value = if (appliedGenreId == -1) {
            null
        } else {
            genres.find { it.id == appliedGenreId }
        }
    }
}