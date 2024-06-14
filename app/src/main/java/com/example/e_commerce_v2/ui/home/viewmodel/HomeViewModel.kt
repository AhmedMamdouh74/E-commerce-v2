package com.example.e_commerce_v2.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.repository.home.SalesAdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(salesAdRepository: SalesAdRepository) : ViewModel() {

    val salesAdsState = salesAdRepository.getSalesAds()
        .stateIn(scope = viewModelScope + IO, started =  SharingStarted.Eagerly, initialValue =  Resource.Loading())

}