package com.balloondigital.egvapp.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class InvitationRequest(
    var id: String = "",
    var leaderId: String = "",
    var leaderName: String = "",
    var requestorEmail: String = "",
    var requestorName: String = "",
    var requestorWhatsapp: String = "",
    var embassy: Embassy? = null
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "leaderId" to leaderId,
            "leaderName" to leaderName,
            "requestorEmail" to requestorEmail,
            "requestorName" to requestorName,
            "requestorWhatsapp" to requestorWhatsapp,
            "embassy" to embassy
        )
    }
}