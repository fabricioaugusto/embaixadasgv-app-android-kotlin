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
    var cover_img_file_name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var status: String? = null,
    var approved_by_id: String? = null,
    var approved_by_name: String? = null,
    var leader_id: String = "",
    var leader_username: String = "",
    var leader: User? = null,
    var embassySponsor_id: String? = null,
    var embassySponsor: EmbassySponsor? = null
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
            "cover_img_file_name" to cover_img_file_name,
            "phone" to phone,
            "email" to email,
            "status" to status,
            "approved_by_id" to approved_by_id,
            "approved_by_name" to approved_by_name,
            "embassySponsor_id" to embassySponsor_id,
            "embassySponsor" to embassySponsor,
            "leader_id" to leader_id,
            "leader_username" to leader_username,
            "leader" to leader?.toBasicMap()
        )
    }
}