package com.balloondigital.egvapp.model
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
    var user: User? = null
    ): Serializable