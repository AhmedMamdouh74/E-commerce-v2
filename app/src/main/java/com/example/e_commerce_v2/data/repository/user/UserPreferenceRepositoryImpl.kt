package com.example.e_commerce_v2.data.repository.user

import android.content.Context
import com.example.e_commerce_v2.data.datasource.datastore.userDetailsDataStore
import com.example.e_commerce_v2.data.models.user.UserDetailsPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor( private val context: Context) :
    UserPreferenceRepository {
    override fun getUserDetails(): Flow<UserDetailsPreferences> {
        return context.userDetailsDataStore.data
    }

    override suspend fun updateUserId(userId: String) {
        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().setId(userId).build()
        }
    }

    override suspend fun getUserId(): Flow<String> {
        return context.userDetailsDataStore.data.map { it.id }
    }

    override suspend fun clearUserPreferences() {
        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }
    }

    override suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences) {
        context.userDetailsDataStore.updateData { userDetailsPreferences }
    }
}