package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Invite(
    var id: String = "",
    var name_sender: String = "",
    var email_sender: String = "",
    var name_receiver: String = "",
    var email_receiver: String = "",
    var invite_code: Int = 0
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "name_sender" to name_sender,
            "email_sender" to email_sender,
            "name_receiver" to name_receiver,
            "email_receiver" to email_receiver,
            "invite_code" to invite_code
        )
    }
}