package com.example.e_commerce_v2.data.repository.auth

import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String, password: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>>
    suspend fun loginWithFacebook(token: String): Flow<Resource<UserDetailsModel>>
    suspend fun registerWithFacebook(token: String): Flow<Resource<UserDetailsModel>>
    suspend fun registerWithGoogle(token: String): Flow<Resource<UserDetailsModel>>
    suspend fun registerWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<UserDetailsModel>>
    suspend fun sendPasswordResetEmail(email: String): Flow<Resource<String>>

    fun logout()

}