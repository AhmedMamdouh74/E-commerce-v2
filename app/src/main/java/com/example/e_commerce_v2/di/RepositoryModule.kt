package com.example.e_commerce_v2.di

import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepository
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.e_commerce_v2.data.repository.common.AppDataStoreRepositoryImpl
import com.example.e_commerce_v2.data.repository.common.AppPreferenceRepository
import com.example.e_commerce_v2.data.repository.home.SalesAdRepository
import com.example.e_commerce_v2.data.repository.home.SalesAdRepositoryImpl
import com.example.e_commerce_v2.data.repository.user.UserFirestoreRepository
import com.example.e_commerce_v2.data.repository.user.UserFirestoreRepositoryImpl
import com.example.e_commerce_v2.data.repository.user.UserPreferenceRepository
import com.example.e_commerce_v2.data.repository.user.UserPreferenceRepositoryImpl
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
    abstract fun bindsUserFirestoreRepository(
        userFirestoreRepositoryImpl: UserFirestoreRepositoryImpl
    ): UserFirestoreRepository

    @Binds
    @Singleton
    abstract fun bindsUserPreferencesRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindsFirebaseAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository

    @Binds
    @Singleton
    abstract fun bindsAppPreferencesRepository(
        appPreferencesRepositoryImpl: AppDataStoreRepositoryImpl
    ): AppPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindsSalesAdRepository(salesAdRepositoryImpl: SalesAdRepositoryImpl): SalesAdRepository
}