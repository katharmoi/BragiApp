package com.test.bragiapp.domain.interactor

import com.test.bragiapp.domain.model.Movie
import com.test.bragiapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(genreId: Int?): Flow<Result<List<Movie>>> {
        return repository.getMovies(genreId)
    }
}