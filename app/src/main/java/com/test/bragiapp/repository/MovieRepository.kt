package com.test.bragiapp.repository

import com.test.bragiapp.domain.model.Genre
import com.test.bragiapp.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getGenres(): Flow<Result<List<Genre>>>
    fun getMovies(genreId: Int?): Flow<Result<List<Movie>>>
}