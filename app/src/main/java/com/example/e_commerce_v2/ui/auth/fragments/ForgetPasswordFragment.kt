package com.example.e_commerce_v2.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.e_commerce_v2.databinding.FragmentForgetPasswordBinding
import com.example.e_commerce_v2.ui.auth.viewmodel.ForgetPasswordViewModel
import com.example.e_commerce_v2.ui.auth.viewmodel.ForgetPasswordViewModelFactory
import com.example.e_commerce_v2.ui.common.customviews.ProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ForgetPasswordFragment : BottomSheetDialogFragment() {
    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }
    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels {
        ForgetPasswordViewModelFactory()
    }
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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ForgetPasswordFragment"
    }
}