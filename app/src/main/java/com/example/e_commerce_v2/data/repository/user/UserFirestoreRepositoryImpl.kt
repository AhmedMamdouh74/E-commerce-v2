package com.example.e_commerce_v2.data.repository.user

import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserFirestoreRepository {

    override suspend fun getUserDetails(userId: String): Flow<Resource<UserDetailsModel>> =
        callbackFlow {
            send(Resource.Loading())
            val documentPath = "users/$userId"
            val document = firestore.document(documentPath)
            val listener = document.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.toObject(UserDetailsModel::class.java)?.let {
                    if(it.disabled == true) {
                        close(AccountDisabledException("Account Disabled"))
                        return@addSnapshotListener
                    }
                    trySend(Resource.Success(it))
                } ?: run {
                    close(UserNotFoundException("User not found"))
                }
            }
            awaitClose {
                listener.remove()
            }
        }
}

class UserNotFoundException(message: String) : Exception(message)
class AccountDisabledException(message: String) : Exception(message)