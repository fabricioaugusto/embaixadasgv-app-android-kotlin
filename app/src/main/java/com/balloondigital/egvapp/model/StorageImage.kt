package com.balloondigital.egvapp.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class StorageImage(
    var id: String = "",
    var name: String = "",
    var path: String = "",
    var extension: String = "",
    var url: String = "",
    var post_id: String? = null,
    var user_id: String? = null,
    var user: User = User()
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "user_id" to user_id,
            "post_id" to post_id,
            "user" to user.toBasicMap()
        )
    }
}