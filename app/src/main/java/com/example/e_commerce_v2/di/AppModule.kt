package com.example.e_commerce_v2.di


import android.app.Application
import android.content.Context
import com.example.e_commerce_v2.data.datasource.datastore.AppPreferencesDataSource
import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppPreferences(context: Application): AppPreferencesDataSource {
        return AppPreferencesDataSource(context)
    }
    @Provides
    @Singleton
    fun fakeUserData(): UserDetailsModel {
        return UserDetailsModel(
            id = "1236", email = "ahmed@mail.com"
        )
    }

}