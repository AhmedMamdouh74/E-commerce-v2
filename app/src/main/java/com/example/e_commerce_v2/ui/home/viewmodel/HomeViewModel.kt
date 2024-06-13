package com.example.e_commerce_v2.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import com.example.e_commerce_v2.data.repository.user.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val userPreferenceRepository: UserPreferenceRepository) :
    ViewModel()