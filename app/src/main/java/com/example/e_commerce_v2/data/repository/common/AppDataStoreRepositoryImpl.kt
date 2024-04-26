package com.example.e_commerce_v2.data.repository.common

import com.example.e_commerce_v2.data.datasource.datastore.AppPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class AppDataStoreRepositoryImpl(
    private val appPreferencesDataSource: AppPreferencesDataSource
) : AppPreferenceRepository {

    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        appPreferencesDataSource.saveLoginState(isLoggedIn)
    }

    override suspend fun isLoggedIn(): Flow<Boolean> {
        return appPreferencesDataSource.isUserLoggedIn
    }
}
