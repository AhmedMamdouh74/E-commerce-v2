package com.example.e_commerce_v2.ui.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.e_commerce_v2.R

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSplashScreen()
        setContentView(R.layout.activity_main)
//        findViewById<TextView>(R.id.TxtView).setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
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
}