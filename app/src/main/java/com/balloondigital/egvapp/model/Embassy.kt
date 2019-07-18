package com.balloondigital.egvapp.model
import java.io.Serializable

data class Embassy (
    val id: String,
    val name: String,
    val city: String,
    val neighborhood: String?,
    val state: String,
    val leader: User
) : Serializable