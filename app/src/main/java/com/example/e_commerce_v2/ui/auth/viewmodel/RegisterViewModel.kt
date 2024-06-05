package com.example.e_commerce_v2.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.models.auth.RegisterRequestModel
import com.example.e_commerce_v2.data.models.auth.RegisterResponseModel
import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepository
import com.example.e_commerce_v2.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {
    private val _registerState = MutableSharedFlow<Resource<RegisterResponseModel>>()
    val registerState: SharedFlow<Resource<RegisterResponseModel>> = _registerState.asSharedFlow()

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
        if (isRegisterIsValid.first()) {
            val registerRequestModel =
                RegisterRequestModel(
                    email = email,
                    password = password,
                    fullName = name
                )
            authRepository.registerEmailAndPasswordWithAPI(
                registerRequestModel
            ).collect {
                _registerState.emit(it)
            }

        } else {


        }


    }


    private fun registerWithEmailAndPassword(name: String, email: String, password: String) =
        viewModelScope.launch(IO) {
//            val result = authRepository.registerWithEmailAndPassword(name, email, password)
//            result.collect { resource ->
//                when (resource) {
//
//                    is Resource.Success -> {
//                        _registerState.emit(Resource.Success(resource.data!!))
//                    }
//
//                    else -> {
//                        _registerState.emit(resource)
//                    }
//                }

        }


    fun registerWithFacebook(token: String) =
        viewModelScope.launch(IO) {
//            val result = authRepository.registerWithFacebook(token)
//            result.collect { resource ->
//                when (resource) {
//
//                    is Resource.Success -> {
//                        _registerState.emit(Resource.Success(resource.data!!))
//                    }
//
//                    else -> {
//                        _registerState.emit(resource)
//                    }
//                }

        }


    fun registerWithGoogle(token: String) =
        viewModelScope.launch(IO) {
//            val result = authRepository.registerWithGoogle(token)
//            result.collect { resource ->
//                when (resource) {
//
//                    is Resource.Success -> {
//                        _registerState.emit(Resource.Success(resource.data!!))
//                    }
//
//                    else -> {
//                        _registerState.emit(resource)
//                    }
//                }
//
//            }
        }


}


