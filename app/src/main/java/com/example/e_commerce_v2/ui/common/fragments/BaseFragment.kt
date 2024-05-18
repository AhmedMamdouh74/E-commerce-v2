package com.example.e_commerce_v2.ui.common.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.e_commerce_v2.BR
import com.example.e_commerce_v2.ui.common.customviews.ProgressDialog

abstract class BaseFragment<DB : ViewDataBinding, VM : ViewModel> : Fragment() {
    val progressDialog by lazy { ProgressDialog.createProgressDialog(requireContext()) }
    protected abstract val viewModel: VM
    protected var _binding: DB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), container, false)
        return binding.root
    }
    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doDataBinding()
        init()
    }


    private fun doDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(
            BR.viewModel,
            viewModel
        )
        binding.executePendingBindings()
    }

    abstract fun init()
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}