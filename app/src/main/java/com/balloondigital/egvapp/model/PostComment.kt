package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class PostComment(
    var id: String = "",
    var post_id: String = "",
    var user_id: String = "",
    var text: String = "",
    var date: Timestamp? = null,
    var user: User = User()
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "post_id" to post_id,
            "user_id" to user_id,
            "text" to text,
            "date" to FieldValue.serverTimestamp(),
            "user" to user.toBasicMap()
        )
    }
}