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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LoginViewModel(
    private val appPreferenceRepository: AppPreferenceRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    private val _loginState = MutableSharedFlow<Resource<UserDetailsModel>?>()
    val loginState: SharedFlow<Resource<UserDetailsModel>?> = _loginState.asSharedFlow()
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun login() {
        viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (isLoginIsValid.first()) {
                handleLoginFlow { authRepository.loginWithEmailAndPassword(email, password) }
            } else {
                _loginState.emit(Resource.Error(Exception("Invalid email or password")))
            }
        }
    }

    fun loginWithGoogle(idToken: String) {

        handleLoginFlow { authRepository.loginWithGoogle(idToken) }
    }

    fun loginWithFacebook(token: String) {
        handleLoginFlow { authRepository.loginWithFacebook(token) }
    }


    private fun handleLoginFlow(loginFlow: suspend () -> Flow<Resource<UserDetailsModel>>) =
        viewModelScope.launch(Dispatchers.IO) {
            loginFlow().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        savePreferenceData(resource.data!!)
                        _loginState.emit(Resource.Success(resource.data))
                    }

                    else -> _loginState.emit(resource)
                }
            }
        }

    private suspend fun savePreferenceData(userDetailsModel: UserDetailsModel) {
        appPreferenceRepository.saveLoginState(true)
        userPreferenceRepository.updateUserDetails(userDetailsModel.toUserDetailsPreferences())
    }


    companion object {
        private const val TAG = "LoginViewModel"
    }
}

// create viewmodel factory class
class LoginViewModelFactory(
    private val contextValue: Context

) : ViewModelProvider.Factory {
    private val appPreferenceRepository =
        AppDataStoreRepositoryImpl(AppPreferencesDataSource(contextValue))
    private val userPreferenceRepository = UserPreferenceRepositoryImpl(contextValue)
    private val authRepository = FirebaseAuthRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(
                appPreferenceRepository,
                userPreferenceRepository,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}