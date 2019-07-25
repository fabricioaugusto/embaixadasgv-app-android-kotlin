package com.balloondigital.egvapp.model
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

data class Embassy (
    var id: String = "",
    var name: String = "",
    var city: String = "",
    var neighborhood: String? = null,
    var state: String = "",
    var leader: DocumentReference? = null
) : Serializable