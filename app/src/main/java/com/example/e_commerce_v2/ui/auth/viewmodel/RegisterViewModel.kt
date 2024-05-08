package com.example.e_commerce_v2.ui.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_v2.data.datasource.datastore.AppPreferencesDataSource
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepository
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.e_commerce_v2.data.repository.common.AppDataStoreRepositoryImpl
import com.example.e_commerce_v2.data.repository.common.AppPreferenceRepository
import com.example.e_commerce_v2.data.repository.user.UserPreferenceRepository
import com.example.e_commerce_v2.data.repository.user.UserPreferenceRepositoryImpl
import com.example.e_commerce_v2.domain.models.toUserDetailsPreferences
import com.example.e_commerce_v2.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RegisterViewModel(
    private val appPreferenceRepository: AppPreferenceRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    private val _registerState = MutableSharedFlow<Resource<UserDetailsModel>?>()
    val registerState: SharedFlow<Resource<UserDetailsModel>?> = _registerState.asSharedFlow()

    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    private val isRegisterIsValid =
        combine(name, email, password, confirmPassword) { name, email, password, confirmPassword ->
            name.isNotEmpty() && email.isValidEmail() && password.isNotEmpty() && confirmPassword == password
        }

    fun register() = viewModelScope.launch(IO) {
        val name = name.value
        val email = email.value
        val password = password.value
        val confirmPassword = confirmPassword.value
        if (isRegisterIsValid.first()) {
            registerWithEmailAndPassword(name, email, password)

        } else {
            _registerState.emit(Resource.Error(Exception("Invalid email or password")))

        }


    }

    private fun registerWithEmailAndPassword(name: String, email: String, password: String) =
        viewModelScope.launch(IO) {
            val result = authRepository.registerWithEmailAndPassword(name, email, password)
            result.collect { resource ->
                resource
                when (resource) {

                    is Resource.Success -> {
                        savePreferenceData(resource.data!!)
                        _registerState.emit(Resource.Success(resource.data!!))
                    }

                    else -> {
                        _registerState.emit(resource)
                    }
                }

            }
        }

     fun registerWithFacebook(token: String) =
        viewModelScope.launch(IO) {
            val result = authRepository.registerWithFacebook(token)
            result.collect { resource ->
                resource
                when (resource) {

                    is Resource.Success -> {
                        savePreferenceData(resource.data!!)
                        _registerState.emit(Resource.Success(resource.data!!))
                    }

                    else -> {
                        _registerState.emit(resource)
                    }
                }

            }
        }

    private suspend fun savePreferenceData(userDetailsModel: UserDetailsModel) {
        appPreferenceRepository.saveLoginState(true)
        userPreferenceRepository.updateUserDetails(userDetailsModel.toUserDetailsPreferences())
    }
}


class RegisterViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    private val appPreferenceRepository =
        AppDataStoreRepositoryImpl(AppPreferencesDataSource(context))
    private val userPreferenceRepository = UserPreferenceRepositoryImpl(context)
    private val authRepository = FirebaseAuthRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return RegisterViewModel(
                appPreferenceRepository,
                userPreferenceRepository,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}