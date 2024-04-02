package com.example.e_commerce_v2.data.repository.auth

import com.example.e_commerce_v2.data.models.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String, password: String
    ): Flow<Resource<String>>

    suspend fun loginWithGoogle(idToken: String): Flow<Resource<String>>
    suspend fun loginWithFacebook(token: String): Flow<Resource<String>>

}