package com.example.e_commerce_v2.data.models.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class UserDetailsModel(
    @ServerTimestamp
    @get:PropertyName("created-at")
    @set:PropertyName("created-at")
    var createdAt: Date? = null,
    var id: String? = null,
    var email: String? = null,
    var name: String? = null,
    var disabled: Boolean? = null,
    var reviews: List<String>? = null,
    var idToken: String? = null
) : Parcelable
