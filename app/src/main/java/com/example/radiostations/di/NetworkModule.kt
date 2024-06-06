package com.example.radiostations.di

import com.example.radiostations.stations.framework.apiservice.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  private const val BASE_URL = "https://de1.api.radio-browser.info/"

  @Provides
  @Singleton
  fun provideRetrofit(): Retrofit =
    Retrofit.Builder().baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}