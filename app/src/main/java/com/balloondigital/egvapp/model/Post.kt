package com.balloondigital.egvapp.model
import android.graphics.Bitmap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Post(
    var id: String = "",
    var type: String = "",
    var date: Timestamp? = null,
    var schedule: String? = null,
    var text: String? = null,
    var picture: String? = null,
    var title: String? = null,
    var post_likes: Int = 0,
    var like_verified: Boolean = false,
    var liked: Boolean = false,
    var list_likes: MutableList<PostLike>? = null,
    var user: BasicUser = BasicUser()
    ): Serializable {
    @Exclude
    fun toMapNote(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "schedule" to schedule,
            "type" to type,
            "title" to title,
            "text" to text,
            "picture" to picture,
            "post_likes" to post_likes,
            "user" to user
        )
    }

    @Exclude
    fun toMapTought(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "schedule" to schedule,
            "type" to type,
            "text" to text,
            "post_likes" to post_likes,
            "user" to user
        )
    }

    @Exclude
    fun toMapPost(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "schedule" to schedule,
            "type" to type,
            "text" to text,
            "picture" to picture,
            "post_likes" to post_likes,
            "user" to user
        )
    }
}