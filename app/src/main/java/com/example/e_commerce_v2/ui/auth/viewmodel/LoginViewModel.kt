package com.example.e_commerce_v2.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepository
import com.example.e_commerce_v2.data.repository.user.UserPreferenceRepository
import com.example.e_commerce_v2.utils.isValidEmail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    val userPrefs: UserPreferenceRepository,
    private val firebaseAuth: FirebaseAuthRepository
) : ViewModel() {
    private val _loginState = MutableSharedFlow<Resource<String>?>()
    val loginState: SharedFlow<Resource<String>?> = _loginState.asSharedFlow()
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
                firebaseAuth.loginWithEmailAndPassword(email, password).onEach { resource ->
                    when (resource) {
                        is Resource.Error ->
                            _loginState.emit(
                                Resource.Error(resource.exception ?: Exception("UnknownError"))
                            )

                        is Resource.Loading -> _loginState.emit(Resource.Loading())
                        is Resource.Success -> _loginState.emit(
                            Resource.Success(resource?.data ?: "")
                        )
                    }
                }.launchIn(viewModelScope)
            } else
                _loginState.emit(Resource.Error(Exception("Invalid Email Or Password")))
        }
    }

    fun loginWithGoogle(idToken: String) {

        viewModelScope.launch {
            firebaseAuth.loginWithGoogle(idToken).onEach { resource ->
                when (resource) {
                    is Resource.Success -> _loginState.emit(Resource.Success(resource.data ?: ""))
                    is Resource.Error -> _loginState.emit(Resource.Error(Exception(resource.exception)))
                    is Resource.Loading ->_loginState.emit(Resource.Loading())
                }
            }.launchIn(viewModelScope)
        }
    }

    fun loginWithFacebook(token: String) {
        viewModelScope.launch {
            firebaseAuth.loginWithFacebook(token).onEach { resource ->
                when (resource) {
                    is Resource.Success -> _loginState.emit(Resource.Success(resource.data ?: ""))
                    else -> _loginState.emit(resource)
                }
            }.launchIn(viewModelScope)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}

// create viewmodel factory class
class LoginViewModelFactory(
    private val userPrefs: UserPreferenceRepository,
    private val firebaseAuth: FirebaseAuthRepository

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPrefs, firebaseAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}