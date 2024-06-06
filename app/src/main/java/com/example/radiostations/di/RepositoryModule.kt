package com.example.radiostations.di

import com.example.radiostations.stations.data.repository.RadioStationRepositoryImpl
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRadioStationRepository(
        radioStationRepositoryImpl: RadioStationRepositoryImpl
    ): RadioStationRepository

}