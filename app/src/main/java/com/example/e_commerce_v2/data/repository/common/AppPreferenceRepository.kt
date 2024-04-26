package com.example.e_commerce_v2.data.repository.common

import kotlinx.coroutines.flow.Flow

interface AppPreferenceRepository {
    suspend fun saveLoginState(isLoggedIn: Boolean)
    suspend fun isLoggedIn(): Flow<Boolean>
}
