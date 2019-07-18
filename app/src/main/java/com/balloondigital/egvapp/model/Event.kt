package com.balloondigital.egvapp.model

import java.io.Serializable

data class Event(
    val id: String,
    val theme: String,
    val description: String,
    val date: String,
    val schedule: String,
    val cover_img: String,
    var observation: String?,
    var street: String?,
    var street_number: String?,
    var neighborhood: String?,
    var city: String?,
    var zipcode: String?,
    var address: String?,
    var place: String?,
    var lat: String?,
    var long: String?,
    var moderator: User,
    val embassy: Embassy
) : Serializable