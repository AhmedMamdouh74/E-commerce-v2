package com.example.e_commerce_v2.domain.models

import com.example.e_commerce_v2.data.models.user.UserDetailsModel
import com.example.e_commerce_v2.data.models.user.UserDetailsPreferences


fun UserDetailsPreferences.toUserDetailsModel(): UserDetailsModel {
    return UserDetailsModel(
        id = id,
        email = email,
        name = name,
        reviews = reviewsList
    )
}

fun UserDetailsModel.toUserDetailsPreferences(): UserDetailsPreferences {
    return UserDetailsPreferences.newBuilder()
        .setId(id)
        .setEmail(email)
        .addAllReviews(reviews?.toList() ?: emptyList())
        .build()
}