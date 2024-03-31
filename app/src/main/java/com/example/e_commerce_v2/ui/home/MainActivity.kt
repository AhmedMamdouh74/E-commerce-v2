package com.example.e_commerce_v2.ui.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.data.datasource.datastore.UserPreferencesDataSource
import com.example.e_commerce_v2.data.repository.user.UserDataStoreRepositoryImpl
import com.example.e_commerce_v2.ui.auth.AuthActivity
import com.example.e_commerce_v2.ui.common.viewmodel.UserViewModel
import com.example.e_commerce_v2.ui.common.viewmodel.UserViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserDataStoreRepositoryImpl(UserPreferencesDataSource(this)))
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSplashScreen()

        lifecycleScope.launch(Dispatchers.Main) {
            val isLoggedIn = userViewModel.isUserLoggedIn().first()
            Log.d(TAG, "onCreate: isLoggedIn: $isLoggedIn")
            if (isLoggedIn) {
                setContentView(R.layout.activity_main)
            } else {
                goToAuthActivity()
            }
        }
        Log.d(TAG, "onCreate: ")
    }
    private fun initSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // Create your custom animation.
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView, View.TRANSLATION_Y, 0f, -splashScreenView.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 1000L

                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.doOnEnd { splashScreenView.remove() }

                // Run your animation.
                slideUp.start()
            }
        } else {
            setTheme(R.style.Theme_ECommerceV2)
        }
    }
    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val options = ActivityOptions.makeCustomAnimation(
            this, android.R.anim.fade_in, android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
    }
    companion object{
        const val TAG="MainActivity"
    }
}