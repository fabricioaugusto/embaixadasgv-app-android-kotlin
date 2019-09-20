package com.balloondigital.egvapp.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class EmbassySponsor (
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var user_id: String = "",
    var user: User = User()
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "user_id" to user_id,
            "user" to user.toBasicMap()
        )
    }
}