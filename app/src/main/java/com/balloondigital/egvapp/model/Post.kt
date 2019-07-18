package com.balloondigital.egvapp.model
import java.io.Serializable

data class Post(
    val id: String,
    val date: String,
    val schedule: String,
    var thought: String?,
    var img: String?,
    var img_description: String?,
    var article_title: String?,
    var article_text: String?,
    val user: User
    ): Serializable