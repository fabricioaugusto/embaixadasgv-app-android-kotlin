package com.balloondigital.egvapp.model

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var status: String? = null,
    var gender: String? = null,
    var description: String? = null,
    var birthdate: String? = null,
    var occupation: String? = null,
    var password: String? = null,
    var city: String? = null,
    var state: String? = null,
    var state_short: String? = null,
    var profile_img: String? = null,
    var profile_img_file_name: String? = null,
    var verified: Boolean = false,
    var facebook: String? = null,
    var twitter: String? = null,
    var instagram: String? = null,
    var linkedin: String? = null,
    var whatsapp: String? = null,
    var youtube: String? = null,
    var behance: String? = null,
    var github: String? = null,
    var website: String? = null,
    var leader: Boolean = false,
    var manager: Boolean = false,
    var sponsor: Boolean = false,
    var username: String? = null,
    var embassy_id: String? = null,
    var embassy: Embassy = Embassy()
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
            "birthdate" to birthdate,
            "occupation" to occupation,
            "city" to city,
            "state" to state,
            "state_short" to state_short,
            "profile_img" to profile_img,
            "profile_img_file_name" to profile_img_file_name,
            "verified" to verified,
            "facebook" to facebook,
            "twitter" to twitter,
            "instagram" to instagram,
            "linkedin" to linkedin,
            "whatsapp" to whatsapp,
            "youtube" to youtube,
            "behance" to behance,
            "github" to github,
            "website" to website,
            "leader" to leader,
            "manager" to manager,
            "sponsor" to sponsor,
            "username" to username,
            "embassy_id" to embassy_id,
            "embassy" to embassy.toBasicMap()
        )
    }
    @Exclude
    fun toBasicMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "profile_img" to profile_img,
            "embassy_id" to embassy_id,
            "occupation" to occupation,
            "username" to username,
            "verified" to verified
        )
    }
}
