package com.test.bragiapp.domain.interactor

import com.test.bragiapp.domain.model.Genre
import com.test.bragiapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetGenresUseCase(private val repository: MovieRepository) {
    operator fun invoke(): Flow<Result<List<Genre>>> {
        return repository.getGenres()
    }
}