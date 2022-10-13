package com.example.movieapplication.models

data class MovieResponse(
    val page: Int?,
    val results: MutableList<Result>?,
    val total_pages: Int?,
    val total_results: Int?
)