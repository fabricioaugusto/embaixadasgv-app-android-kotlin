package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
data class AppMessage(
    var id: String = "",
    var user_id: String = "",
    var user_embassy: String = "",
    var user_city: String = "",
    var message: String = "",
    var user: User = User(),
    var date: Timestamp? = null
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "user_id" to user_id,
            "user_embassy" to user_embassy,
            "user_city" to user_city,
            "user" to user.toBasicMap(),
            "message" to message
        )
    }
}