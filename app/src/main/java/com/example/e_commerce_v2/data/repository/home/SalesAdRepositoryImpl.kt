package com.example.e_commerce_v2.data.repository.home

import android.util.Log
import com.example.e_commerce_v2.data.models.Resource
import com.example.e_commerce_v2.data.models.sales_ads.SalesAdModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SalesAdRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    SalesAdRepository {
    override fun getSalesAds() = flow {

        try {
            Log.d(TAG, "getSalesAds: ")
            emit(Resource.Loading())
            val saleAds =
                firestore.collection("sales_ads").get().await().toObjects(SalesAdModel::class.java)



            emit(Resource.Success(saleAds.map { it.toUIModel() }))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }

    }
    companion object{
        const val TAG = "SalesAdRepositoryImpl"
    }
}