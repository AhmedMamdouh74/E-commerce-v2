package com.example.e_commerce_v2.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_v2.BuildConfig
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.databinding.FragmentLoginBinding
import com.example.e_commerce_v2.ui.auth.viewmodel.LoginViewModel
import com.example.e_commerce_v2.ui.auth.viewmodel.LoginViewModelFactory
import com.example.e_commerce_v2.ui.common.customviews.ProgressDialog
import com.example.e_commerce_v2.ui.showSnakeBarError
import com.example.e_commerce_v2.utils.CrashlyticsUtils
import com.example.e_commerce_v2.utils.LoginException
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }
    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }


    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            requireContext()

        )
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initListeners()


    }

    private fun initViewModel() {
        lifecycleScope.launch {

            loginViewModel.loginState.collect { state ->
                state?.let { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            val msg = resource.exception?.message
                                ?: getString(R.string.generic_err_msg)
                            progressDialog.dismiss()
                            view?.showSnakeBarError(
                                msg
                            )
                            logAuthIssueToCrashlytics(msg, "Login Error")
                        }

                        is Resource.Loading -> {
                            progressDialog.show()
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()


                        }

                    }

                }
            }
        }
    }

    private fun initListeners() {
        binding.googleSigninBtn.setOnClickListener {
            signInWithGoogle()

        }
        binding.facebookSigninBtn.setOnClickListener {
            signInWithFacebook()
        }
        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    // ActivityResultLauncher for the sign-in intent
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
            }
        }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.clientServerId).requestEmail().requestProfile()
            .requestServerAuthCode(BuildConfig.clientServerId).build()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))
            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        loginViewModel.loginWithGoogle(idToken)
    }


    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider,
        )
    }

    private fun signOut() {
        loginManager.logOut()
        Log.d(TAG, "signOut: ")
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    private fun signInWithFacebook() {
        if (isLoggedIn()) signOut()
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "onSuccess: ")
                    val token = loginResult.accessToken.token
                    handleFacebookAccessToken(token)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")

                }

                override fun onError(error: FacebookException) {
                    view?.showSnakeBarError(error.message ?: getString(R.string.generic_err_msg))
                    val msg = error.message ?: getString(R.string.generic_err_msg)
                    logAuthIssueToCrashlytics(msg, "Facebook")
                }
            },
        )


        loginManager.logInWithReadPermissions(
            this,
            callbackManager,
            listOf("email", "public_profile"),

            )

    }

    // ...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: String) {
        loginViewModel.loginWithFacebook(token)
    }

    companion object {

        private const val TAG = "LoginFragment"

    }


}