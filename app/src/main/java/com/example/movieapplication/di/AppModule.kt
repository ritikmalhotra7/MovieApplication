package com.example.movieapplication.di

import com.example.movieapplication.api.MovieApi
import com.example.movieapplication.database.DefaultRepository
import com.example.movieapplication.database.MovieRepository
import com.example.movieapplication.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMovieApiHelper(movieApi: MovieApi): MovieRepository = DefaultRepository(movieApi)

    @Singleton
    @Provides
    fun getApi(): MovieApi {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder().run {
            baseUrl(Constants.BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(client)
            build()
        }
        return retrofit.create(MovieApi::class.java)
    }
}