package com.example.e_commerce_v2.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepository
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.e_commerce_v2.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(val authRepository: FirebaseAuthRepository) : ViewModel() {

    private val _forgetPasswordState = MutableSharedFlow<Resource<String>>()
    val forgetPasswordState: SharedFlow<Resource<String>> = _forgetPasswordState.asSharedFlow()

    val email = MutableStateFlow("")

    fun sendUpdatePasswordEmail() = viewModelScope.launch(IO) {
        if (email.value.isValidEmail()) {
            authRepository.sendPasswordResetEmail(email.value).collect {
                _forgetPasswordState.emit(it)
            }
        } else {
            _forgetPasswordState.emit(Resource.Error(Exception("Invalid email")))
        }
    }
}

class ForgetPasswordViewModelFactory(private val authRepository: FirebaseAuthRepository = FirebaseAuthRepositoryImpl()) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgetPasswordViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}