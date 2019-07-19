package com.balloondigital.egvapp.model

import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val email: String,
    val status: String,
    val gender: String,
    var description: String? = null,
    var password: String? = null,
    var city: String? = null,
    var profile_img: String? = null,
    var facebook: String? = null,
    var twitter: String? = null,
    var instagram: String? = null,
    var linkedin: String? = null,
    var whatsapp: String? = null,
    var youtube: String? = null,
    var website: String? = null,
    var embassy: Embassy? = null
) : Serializable
