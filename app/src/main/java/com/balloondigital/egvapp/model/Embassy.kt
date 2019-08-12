package com.balloondigital.egvapp.model
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Embassy (
    var id: String = "",
    var name: String = "",
    var city: String = "",
    var neighborhood: String? = null,
    var state: String = "",
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
            "leader" to leader?.toBasicMap()
        )
    }
}