package com.example.movieapplication.database

import com.example.movieapplication.api.MovieApi
import com.example.movieapplication.models.MovieResponse
import com.example.movieapplication.utils.Resources
import javax.inject.Inject

class DefaultRepository @Inject constructor(private val movieApi:MovieApi) :MovieRepository{
    override suspend fun  popularMovies(page:Int) = movieApi.getPopular(page = page)
    override suspend fun  topRatedMovies(page:Int) = movieApi.getTopRated(page = page)
    override suspend fun getSimilar(id:Int) = movieApi.getSimilar(id)
    override suspend fun getSearchDetails(query:String, page:Int) = movieApi.getSearched(query = query, page = page)
    override suspend fun getDetails(id :Int) = movieApi.getDetails(id)
    override suspend fun getImages(id:Int) = movieApi.getImages(id)
    override suspend fun getMovieWithGenre(id:Int) = movieApi.getMovieViaGenre(genre = id)
}