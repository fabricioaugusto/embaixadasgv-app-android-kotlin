package com.balloondigital.egvapp.model
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Embassy (
    var id: String = "",
    var name: String = "",
    var city: String = "",
    var neighborhood: String? = "",
    var state: String = "",
    var state_short: String = "",
    var cover_img: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var leader_id: String = "",
    var leader: User? = null
) : Serializable {
    @Exclude
    fun toBasicMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name
        )
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "city" to city,
            "neighborhood" to neighborhood,
            "state" to state,
            "state_short" to state,
            "cover_img" to cover_img,
            "phone" to phone,
            "email" to email,
            "leader_id" to leader_id,
            "leader" to leader?.toBasicMap()
        )
    }
}