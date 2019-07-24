package com.balloondigital.egvapp.model

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var status: String? = null,
    var gender: String? = null,
    var description: String? = null,
    var password: String? = null,
    var city: String? = null,
    var state: String? = null,
    var profile_img: String? = null,
    var facebook: String? = null,
    var twitter: String? = null,
    var instagram: String? = null,
    var linkedin: String? = null,
    var whatsapp: String? = null,
    var youtube: String? = null,
    var website: String? = null,
    @Transient
    var embassy: DocumentReference? = null
) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "status" to status,
            "gender" to gender,
            "description" to description,
            "city" to city,
            "state" to state,
            "profile_img" to profile_img,
            "facebook" to facebook,
            "twitter" to twitter,
            "instagram" to instagram,
            "linkedin" to linkedin,
            "whatsapp" to whatsapp,
            "youtube" to youtube,
            "website" to website,
            "embassy" to embassy
        )
    }
}
