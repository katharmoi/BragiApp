package com.test.bragiapp.data.repository

import android.util.Log
import com.test.bragiapp.data.remote.ApiService
import com.test.bragiapp.data.remote.dto.GenreDto
import com.test.bragiapp.data.remote.dto.MovieDetailsDto
import com.test.bragiapp.data.remote.dto.MovieResultDto
import com.test.bragiapp.domain.model.Genre
import com.test.bragiapp.domain.model.Movie
import com.test.bragiapp.domain.repository.MovieRepository
import com.test.bragiapp.util.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class MovieRepositoryImpl(private val apiService: ApiService) : MovieRepository {

    override fun getGenres(): Flow<Result<List<Genre>>> = flow {
        try {
            val response = apiService.getGenres()
            emit(Result.success(response.genres.map { it.toDomainGenre() }))
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch genres")
            emit(Result.failure(e))
        }
    }

    override fun getMovies(genreId: Int?): Flow<Result<List<Movie>>> = flow {
        try {
            val movieListResponse = apiService.getMovies(genreId = genreId)
            coroutineScope {
                val moviesWithDetails = movieListResponse.results.map { movieResultDto ->
                    async {
                        try {
                            val details = apiService.getMovieDetails(movieResultDto.id)
                            movieResultDto.toDomainMovie(details)
                        } catch (e: Exception) {
                            Timber.w(
                                e,
                                "Failed to fetch details for movie ID ${movieResultDto.id}, returning partial data."
                            )
                            movieResultDto.toDomainMovie()
                        }
                    }
                }.map { it.await() }
                emit(Result.success(moviesWithDetails))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch movies for genreId: $genreId")
            emit(Result.failure(e))
        }
    }

    private fun MovieResultDto.toDomainMovie(details: MovieDetailsDto? = null): Movie {
        return Movie(
            id = this.id,
            title = this.title,
            posterUrl = this.posterPath?.let { "${Constants.TMDB_IMAGE_BASE_URL}$it" },
            rating = this.voteAverage,
            budget = details?.budget,
            revenue = details?.revenue
        )
    }

    private fun GenreDto.toDomainGenre(): Genre {
        return Genre(id = this.id, name = this.name)
    }
}