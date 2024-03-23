package com.example.e_commerce_v2.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.ui.auth.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

    }
}