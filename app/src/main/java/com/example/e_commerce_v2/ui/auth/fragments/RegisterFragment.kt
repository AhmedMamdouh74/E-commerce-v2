package com.example.e_commerce_v2.ui.auth.fragments

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
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.databinding.FragmentRegisterBinding
import com.example.e_commerce_v2.ui.auth.getGoogleRequestIntent
import com.example.e_commerce_v2.ui.auth.viewmodel.RegisterViewModel
import com.example.e_commerce_v2.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.e_commerce_v2.ui.common.customviews.ProgressDialog
import com.example.e_commerce_v2.ui.showSnakeBarError
import com.example.e_commerce_v2.utils.CrashlyticsUtils
import com.example.e_commerce_v2.utils.RegisterException
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(requireContext())
    }
    private val progressDialog by lazy {
        ProgressDialog.createProgressDialog(requireActivity())
    }
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }
    private val binding get() = _binding!!

    // ActivityResultLauncher for the sign-in intent
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignUpResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = registerViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initListeners()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            registerViewModel.registerState.collect { state ->
                state?.let { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            val msg =
                                resource.exception?.message ?: getString(R.string.generic_err_msg)
                            progressDialog.dismiss()
                            view?.showSnakeBarError(msg)
                            logAuthIssueToCrashlytics(msg, "Register Error")

                        }

                        is Resource.Loading -> {
                            progressDialog.show()

                        }

                        is Resource.Success -> {
                            showLoginSuccessfulDialog()
                            progressDialog.dismiss()
                        }

                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.signInTv.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.facebookSignupBtn.setOnClickListener {
            signUpWithFacebook()
        }
        binding.googleSignupBtn.setOnClickListener {
            signUpWithGoogle()
        }


    }


    private fun signUpWithGoogle() {
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }


    private fun handleSignUpResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))
            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun firebaseAuthWithGoogle(token: String) {
        registerViewModel.registerWithGoogle(token)
    }

    private fun signOut() {
        loginManager.logOut()
        Log.d(TAG, "signOut: ")
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }


    private fun signUpWithFacebook() {
        if (isLoggedIn()) signOut()
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val token = loginResult.accessToken.token
                    Log.d(TAG, "onSuccess: $token")
                    firebaseAuthWithFacebook(token)
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


    private fun firebaseAuthWithFacebook(token: String) {
        registerViewModel.registerWithFacebook(token)
    }


    private fun showLoginSuccessfulDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Register success")
            .setMessage("we have sent you an email verification link. please verify your email to login.")
            .setPositiveButton(
                "OK"
            ) { dialog, _ ->
                dialog?.dismiss()
                findNavController().popBackStack()
            }
            .create()
            .show()

    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<RegisterException>(
            msg,
            CrashlyticsUtils.REGISTER_KEY to msg,
            CrashlyticsUtils.REGISTER_PROVIDER to provider,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}