package com.balloondigital.egvapp.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class  BasicUser (
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var profile_img: String? = null
    ): Serializable

