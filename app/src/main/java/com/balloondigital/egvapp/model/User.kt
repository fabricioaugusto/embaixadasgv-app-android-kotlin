package com.balloondigital.egvapp.model

import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val email: String,
    var description: String?,
    var password: String?,
    var city: String?,
    var profile_img: String?,
    var facebook: String?,
    var twitter: String?,
    var instagram: String?,
    var linkedin: String,
    var whatsapp: String,
    var youtube: String,
    var website: String,
    var embassy: Embassy
) : Serializable
