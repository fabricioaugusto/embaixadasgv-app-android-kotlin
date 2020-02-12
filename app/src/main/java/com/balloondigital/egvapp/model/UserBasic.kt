package com.balloondigital.egvapp.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserBasic(
    var id: String = "",
    var name: String = "",
    var description: String? = null,
    var profile_img: String? = null
)