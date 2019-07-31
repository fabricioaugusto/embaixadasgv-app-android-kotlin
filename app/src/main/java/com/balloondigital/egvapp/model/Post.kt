package com.balloondigital.egvapp.model
import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.sql.Timestamp

data class Post(
    var id: String = "",
    var type: String = "",
    var date: Timestamp? = null,
    var schedule: String? = null,
    var thought: String? = null,
    var picture: String? = null,
    var picture_description: String? = null,
    var article_title: String? = null,
    var article_text: String? = null,
    var article_cover: String? = null,
    var post_likes: Int = 0,
    var user: BasicUser = BasicUser()
    ): Serializable {
    @Exclude
    fun toMapNote(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to date,
            "schedule" to schedule,
            "type" to type,
            "article_title" to article_title,
            "article_text" to article_text,
            "article_cover" to article_cover,
            "post_likes" to post_likes,
            "user" to user
        )
    }

    @Exclude
    fun toMapTought(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to date,
            "schedule" to schedule,
            "type" to type,
            "thought" to thought,
            "post_likes" to post_likes,
            "user" to user
        )
    }

    @Exclude
    fun toMapPost(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to date,
            "schedule" to schedule,
            "type" to type,
            "picture" to picture,
            "picture_description" to picture_description,
            "post_likes" to post_likes,
            "user" to user
        )
    }
}