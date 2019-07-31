package com.balloondigital.egvapp.model
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Post(
    var id: String = "",
    var date: String = "",
    var schedule: String = "",
    var type: String = "",
    var thought: String? = null,
    var picture: String? = null,
    var picture_description: String? = null,
    var article_title: String? = null,
    var article_text: String? = null,
    var article_cover: String? = null,
    var user: BasicUser? = null
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
            "user" to user
        )
    }
}