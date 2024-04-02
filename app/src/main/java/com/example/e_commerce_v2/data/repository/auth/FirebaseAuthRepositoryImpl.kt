package com.example.e_commerce_v2.data.repository.auth

import com.example.e_commerce_v2.data.models.Resource
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) :
    FirebaseAuthRepository {
    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            val firebaseAuthResult = auth.signInWithEmailAndPassword(email, password).await()
            firebaseAuthResult.user?.let { user ->
                emit(Resource.Success(user.uid)) // emit the user ID
            } ?: run {
                emit(Resource.Error(Exception("User not found")))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val firebaseAuthResult = auth.signInWithCredential(firebaseCredential).await()
            firebaseAuthResult.user?.let { user ->
                emit(Resource.Success(user.uid)) // emit the user ID
            } ?: run {
                emit(Resource.Error(Exception("User not found")))
            }


        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun loginWithFacebook(token: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val firebaseCredential = FacebookAuthProvider.getCredential(token)
            val firebaseAuthResult= auth.signInWithCredential(firebaseCredential).await()
            firebaseAuthResult.user?.let { user ->
                emit(Resource.Success(user.uid)) // emit the user ID
            } ?: run {
                emit(Resource.Error(Exception("User not found")))
            }

        }catch (e: Exception){
            emit(Resource.Error(e))
        }
    }

    companion object {
        private const val TAG = "FirebaseAuthRepositoryI"
    }
}