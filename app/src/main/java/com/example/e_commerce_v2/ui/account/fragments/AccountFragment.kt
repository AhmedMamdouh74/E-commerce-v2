package com.example.e_commerce_v2.ui.account.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_v2.R
import com.example.e_commerce_v2.ui.auth.AuthActivity
import com.example.e_commerce_v2.ui.common.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels()
    lateinit var textView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView = view.findViewById(R.id.textView)
        textView.setOnClickListener {
            lifecycleScope.launch { viewModel.logOut() }
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()

        }
    }

    companion object {
        const val TAG = "AccountFragment"
    }
}