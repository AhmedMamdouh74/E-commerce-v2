package com.example.e_commerce_v2.data.repository.auth

import android.util.Log
import com.example.e_commerce_v2.data.datasource.networking.CloudFunctionAPI
import com.example.e_commerce_v2.data.datasource.networking.handleErrorResponse
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.models.auth.RegisterRequestModel
import com.example.e_commerce_v2.data.models.auth.RegisterResponseModel
import com.example.e_commerce_v2.data.models.user.AuthProvider
import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import com.example.e_commerce_v2.utils.CrashlyticsUtils
import com.example.e_commerce_v2.utils.LoginException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val cloudFunctionAPI: CloudFunctionAPI
) :
    FirebaseAuthRepository {
    private suspend fun login(
        provider: AuthProvider,
        signInRequest: suspend () -> AuthResult,
    ): Flow<Resource<UserDetailsModel>> = flow {
        try {
            emit(Resource.Loading())
            // perform firebase auth sign in request
            val authResult = signInRequest()
            val userId = authResult.user?.uid

            if (userId == null) {
                val msg = "Sign in UserID not found"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }
            val idTokenRequest = authResult.user?.getIdToken(true)?.await()
            Log.d(TAG, "login: ${idTokenRequest?.token.toString()}")
            if (authResult.user?.isEmailVerified == false) {
                authResult.user?.sendEmailVerification()?.await()
                val msg = "Email not verified, verification email sent"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            // get user details from firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                val msg = "Logged in user not found in firestore"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            // map user details to UserDetailsModel
            val userDetails = userDoc.toObject(UserDetailsModel::class.java)
            userDetails?.let {
                emit(Resource.Success(userDetails))
            } ?: run {
                val msg = "Error mapping user details to UserDetailsModel, user id = $userId"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
            }
        } catch (e: Exception) {
            logAuthIssueToCrashlytics(
                e.message ?: "Unknown error from exception = ${e::class.java}", provider.name
            )
            emit(Resource.Error(e)) // Emit error
        }
    }

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ) = login(AuthProvider.EMAIL) { auth.signInWithEmailAndPassword(email, password).await() }

    override suspend fun loginWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>> =
        login(AuthProvider.GOOGLE) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
        }

    override suspend fun loginWithFacebook(token: String): Flow<Resource<UserDetailsModel>> =
        login(AuthProvider.FACEBOOK) {
            val credential = FacebookAuthProvider.getCredential(token)
            auth.signInWithCredential(credential).await()
        }

    override suspend fun registerWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<UserDetailsModel>> = flow {
        try {
            emit(Resource.Loading())

            // Perform Firebase Auth sign in
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId == null) {
                val msg = "Sign in UserID not found"
                logAuthIssueToCrashlytics(msg, AuthProvider.EMAIL.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }
            val userDetails = UserDetailsModel(
                id = userId,
                email = email,
                name = name
            )

            // Save user details to Firestore
            firestore.collection("users").document(userId).set(userDetails).await()

            // Retrieve the user details after saving
            val updatedUserDoc = firestore.collection("users").document(userId).get().await()
            val retrievedUserDetails = updatedUserDoc.toObject(UserDetailsModel::class.java)
            authResult.user?.sendEmailVerification()?.await()

            // Emit success with retrieved details (optional)
            retrievedUserDetails?.let {
                emit(Resource.Success(it))
            } ?: run {
                val msg = "Error retrieving user details after registration, user id = $userId"
                logAuthIssueToCrashlytics(msg, AuthProvider.EMAIL.name)
                emit(Resource.Error(Exception(msg)))
            }

        } catch (e: Exception) {
            logAuthIssueToCrashlytics(
                e.message ?: "Unknown error from exception = ${e::class.java}",
                AuthProvider.EMAIL.name
            )
            emit(Resource.Error(e)) // Emit error
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success("Password reset email sent"))

        } catch (e: Exception) {
            emit(Resource.Error(e)) // Emit error
        }
    }

    override suspend fun registerWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>> =
        flow {
            try {
                emit(Resource.Loading())
                // Perform Firebase Auth sign in

                val authResult =
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                        .await()
                val msg = "Sign up UserID not found"
                val userId = authResult.user?.uid
                if (userId == null) {
                    logAuthIssueToCrashlytics(msg, AuthProvider.EMAIL.name)
                    emit(Resource.Error(Exception(msg)))
                    return@flow
                }

                val userDetails = UserDetailsModel(
                    id = userId,
                    email = authResult.user?.email,
                    name = authResult.user?.displayName
                )
                // Save user details to Firestore
                firestore.collection("users").document(userId).set(userDetails).await()
                // Retrieve the user details after saving
                val updatedUserDoc = firestore.collection("users").document(userId).get().await()
                val retrievedUserDetails = updatedUserDoc.toObject(UserDetailsModel::class.java)
                authResult.user?.sendEmailVerification()?.await()

                // Emit success with retrieved details (optional)
                retrievedUserDetails?.let {
                    emit(Resource.Success(it))
                } ?: run {
                    val msg = "Error retrieving user details after registration, user id = $userId"
                    logAuthIssueToCrashlytics(msg, AuthProvider.GOOGLE.name)
                    emit(Resource.Error(Exception(msg)))
                }


            } catch (e: Exception) {
                logAuthIssueToCrashlytics(
                    e.message ?: "Unknown error from exception = ${e::class.java}",
                    AuthProvider.GOOGLE.name
                )
                emit(Resource.Error(e)) // Emit error
            }
        }

    override suspend fun registerEmailAndPasswordWithAPI(registerRequestModel: RegisterRequestModel): Flow<Resource<RegisterResponseModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                val response = cloudFunctionAPI.registerUser(registerRequestModel)
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    registerResponse?.data?.let {
                        emit(Resource.Success(it))
                    } ?: run {
                        emit(Resource.Error(Exception(registerResponse?.message)))
                    }
                } else {
                    Log.d(
                        TAG,
                        "registerEmailAndPasswordWithAPI: Error registering user = ${response.errorBody()}"
                    )
                    emit(Resource.Error(Exception(handleErrorResponse(response.errorBody()!!.charStream()))))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e))
            }
        }

    }

    override suspend fun registerWithFacebook(token: String): Flow<Resource<UserDetailsModel>> =
        flow {
            try {
                emit(Resource.Loading())

                // Perform Firebase Auth sign in
                val authResult =
                    auth.signInWithCredential(FacebookAuthProvider.getCredential(token)).await()
                val userId = authResult.user?.uid
                if (userId == null) {
                    val msg = "Sign up UserID not found"
                    logAuthIssueToCrashlytics(msg, AuthProvider.FACEBOOK.name)
                    emit(Resource.Error(Exception(msg)))
                    return@flow
                }
                val userDetails = UserDetailsModel(
                    id = userId,
                    email = authResult.user?.email,
                    name = authResult.user?.displayName
                )
                // Save user details to Firestore
                firestore.collection("users").document(userId).set(userDetails).await()
                // Retrieve the user details after saving
                val updatedUserDoc = firestore.collection("users").document(userId).get().await()
                val retrievedUserDetails = updatedUserDoc.toObject(UserDetailsModel::class.java)
                // Emit success with retrieved details (optional)
                retrievedUserDetails?.let {
                    emit(Resource.Success(it))
                }
            } catch (e: Exception) {
                logAuthIssueToCrashlytics(
                    e.message ?: "Unknown error from exception = ${e::class.java}",
                    AuthProvider.EMAIL.name
                )
                emit(Resource.Error(e)) // Emit error
            }
        }


    override fun logout() {
        auth.signOut()
    }


    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider,
        )
    }


    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }
}
