package com.test.bragiapp.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val rating: Double,
    val budget: Long?,
    val revenue: Long?
)