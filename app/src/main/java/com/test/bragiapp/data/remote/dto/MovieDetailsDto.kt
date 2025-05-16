package com.test.bragiapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailsDto(
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    val budget: Long?,
    val revenue: Long?
)
