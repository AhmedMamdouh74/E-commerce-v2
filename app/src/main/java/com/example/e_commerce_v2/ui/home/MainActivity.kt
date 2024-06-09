package com.example.e_commerce_v2.ui.home


import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.databinding.ActivityMainBinding
import com.example.e_commerce_v2.ui.auth.AuthActivity
import com.example.e_commerce_v2.ui.cart.fragments.CartFragment
import com.example.e_commerce_v2.ui.common.viewmodel.UserViewModel
import com.example.e_commerce_v2.ui.explore.fragments.ExploreFragment
import com.example.e_commerce_v2.ui.home.adapter.HomeViewPager
import com.example.e_commerce_v2.ui.home.fragments.HomeFragment
import com.example.e_commerce_v2.ui.offers.fragments.OfferFragment
import com.example.e_commerce_v2.ui.account.fragments.AccountFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        val isLoggedIn = runBlocking { userViewModel.isUserLoggedIn().first() }
        if (!isLoggedIn) {
            goToAuthActivity()
            return
        }
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initViews()
        initViewModel()
    }

    private fun initViews() {
        initViewPager()
        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> binding.homeViewPager.currentItem = 0
                R.id.exploreFragment -> binding.homeViewPager.currentItem = 1
                R.id.cartFragment -> binding.homeViewPager.currentItem = 2
                R.id.offerFragment -> binding.homeViewPager.currentItem = 3
                R.id.accountFragment -> binding.homeViewPager.currentItem = 4


            }
            true
        }

    }

    private fun initViewPager() {
        val fragments = listOf(
            HomeFragment(),
            ExploreFragment(),
            CartFragment(),
            OfferFragment(),
            AccountFragment()
        )
        binding.homeViewPager.offscreenPageLimit = fragments.size
        binding.homeViewPager.adapter = HomeViewPager(this, fragments)
        binding.homeViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            val userDetails = runBlocking { userViewModel.getUserDetails().first() }
            Log.d(TAG, "initViewModel: user details ${userDetails.email}")

            userViewModel.userDetailsState.collect {
                Log.d(TAG, "initViewModel: user details updated ${it?.email}")
            }

        }
    }

    private fun logOut() {
        lifecycleScope.launch {
            userViewModel.logOut()
            goToAuthActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
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

    private fun initSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        } else {
            setTheme(R.style.Theme_ECommerceV2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}