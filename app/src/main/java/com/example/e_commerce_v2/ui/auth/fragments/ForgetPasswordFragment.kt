package com.example.e_commerce_v2.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.databinding.FragmentForgetPasswordBinding
import com.example.e_commerce_v2.ui.auth.viewmodel.ForgetPasswordViewModel
import com.example.e_commerce_v2.ui.common.customviews.ProgressDialog
import com.example.e_commerce_v2.ui.showSnakeBarError
import com.example.e_commerce_v2.utils.CrashlyticsUtils
import com.example.e_commerce_v2.utils.ForgetPasswordException
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordFragment : BottomSheetDialogFragment() {
    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }
    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels ()
    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = forgetPasswordViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            forgetPasswordViewModel.forgetPasswordState.collect { state ->
                when (state) {
                    is Resource.Error -> {
                        val msg = state.exception?.message ?: "Error Please try again"
                        progressDialog.dismiss()
                        view?.showSnakeBarError(msg)

                    }

                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        showSentEmailDialog()
                    }
                }

            }
        }
    }

    private fun showSentEmailDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Password reset ")
            .setMessage("A password reset link has been sent to your email")
            .setPositiveButton(
                "OK"
            ) { dialog, _ ->
                dialog?.dismiss()
                this@ForgetPasswordFragment.dismiss()
            }
            .create()
            .show()

    }

    private fun logIssueToCrashlytics(msg: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<ForgetPasswordException>(
            msg,
            CrashlyticsUtils.FORGOT_PASSWORD_KEY to msg
        )


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ForgetPasswordFragment"
    }
}