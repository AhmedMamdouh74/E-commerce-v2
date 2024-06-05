package com.example.e_commerce_v2.di

import com.example.e_commerce_v2.data.datasource.networking.CloudFunctionAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Provides
    @Singleton
    fun provideCloudFunctionApi(): CloudFunctionAPI {
        return CloudFunctionAPI.create()
    }
}