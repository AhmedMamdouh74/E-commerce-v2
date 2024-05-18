package com.example.e_commerce_v2.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.e_commerce_v2.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

fun getGoogleRequestIntent(context: Activity): Intent {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.clientServerId).requestEmail().requestProfile()
        .requestServerAuthCode(BuildConfig.clientServerId).build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()
    val signInIntent = googleSignInClient.signInIntent
    return signInIntent
}