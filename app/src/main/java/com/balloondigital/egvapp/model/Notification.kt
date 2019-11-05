package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Notification (
    var id: String = "",
    var receiver_id: String = "",
    var title: String = "",
    var description: String = "",
    var text: String = "",
    var picture: String = "",
    var type:  String = "",
    var read: Boolean = true,
    var comment_id: String? = null,
    var like_id: String? = null,
    var relationship_id: String? = null,
    var only_leaders: Boolean = false,
    var post_id: String? = null,
    var company_id: String? = null,
    var event_id: String? = null,
    var sender_id: String? = null,
    var created_at: Timestamp? = null

    ): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "created_at" to FieldValue.serverTimestamp(),
            "receiver_id" to receiver_id,
            "title" to title,
            "sender_id" to sender_id,
            "description" to description,
            "text" to text,
            "picture" to picture,
            "type" to type,
            "read" to read,
            "comment_id" to comment_id,
            "like_id" to like_id,
            "relationship_id" to relationship_id,
            "only_leaders" to only_leaders,
            "post_id" to post_id,
            "company_id" to company_id,
            "event_id" to event_id
        )
    }
}