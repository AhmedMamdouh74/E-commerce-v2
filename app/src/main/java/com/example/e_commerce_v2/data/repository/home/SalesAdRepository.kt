package com.example.e_commerce_v2.data.repository.home

import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.ui.home.model.SalesAdUIModel
import kotlinx.coroutines.flow.Flow

interface SalesAdRepository {
     fun  getSalesAds(): Flow<Resource<List<SalesAdUIModel>>>

}