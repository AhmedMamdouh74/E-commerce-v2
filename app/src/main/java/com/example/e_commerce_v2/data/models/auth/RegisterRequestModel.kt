package com.example.e_commerce_v2.data.models.auth

data class RegisterRequestModel(
    val email: String,
    val password: String,
    val fullName: String
)