package com.balloondigital.egvapp.model

import java.io.Serializable

data class Event(
    val id: String,
    val theme: String,
    val tag: String,
    val description: String,
    val date: String,
    val schedule: String,
    var place: String,
    val cover_img: String? = null,
    var observation: String? = null,
    var street: String? = null,
    var street_number: String? = null,
    var neighborhood: String? = null,
    var city: String? = null,
    var zipcode: String? = null,
    var address: String? = null,
    var lat: String? = null,
    var long: String? = null,
    var moderator: User? = null,
    var embassy: Embassy? = null
) : Serializable