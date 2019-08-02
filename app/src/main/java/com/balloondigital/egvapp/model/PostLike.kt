package com.balloondigital.egvapp.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class PostLike(
    var id: String = "",
    var post_id: String = "",
    var user_id: String = "",
    var user: BasicUser? = null
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "post_id" to post_id,
            "user" to user
        )
    }
}