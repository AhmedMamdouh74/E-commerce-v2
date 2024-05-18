package com.example.e_commerce_v2.utils
// create extension function for email validation

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}