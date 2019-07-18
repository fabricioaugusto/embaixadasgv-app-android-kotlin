package com.balloondigital.egvapp.model
import java.io.Serializable

data class Company (
    val id: String,
    val name: String,
    val email: String,
    var phone: String?,
    var description: String?,
    var password: String?,
    var street: String?,
    var street_number: String?,
    var neighborhood: String?,
    var city: String?,
    var zipcode: String?,
    var address: String?,
    var place: String?,
    var lat: String?,
    var long: String?,
    var profile_img: String?,
    var facebook: String?,
    var twitter: String?,
    var instagram: String?,
    var linkedin: String,
    var whatsapp: String,
    var youtube: String,
    var website: String
): Serializable