package com.example.radiostations.di

import com.example.radiostations.stations.domain.usecases.GetRadioStation
import com.example.radiostations.stations.domain.usecases.GetRadioStationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetRadioStation(
        getRadioStationImpl: GetRadioStationImpl
    ): GetRadioStation
}