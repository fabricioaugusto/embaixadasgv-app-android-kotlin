package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Enrollment (
    var id: String = "",
    var event_id: String = "",
    var user_id: String = "",
    var event_date: Timestamp? = null,
    var event: Event = Event(),
    var waiting_list: Boolean = false,
    var user: User = User()
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "user_id" to user_id,
            "event_id" to event_id,
            "event_date" to event_date,
            "enroll_date" to FieldValue.serverTimestamp(),
            "waiting_list" to waiting_list,
            "user" to user.toBasicMap(),
            "event" to event.toBasicMap()
        )
    }
}