package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Event(
    var id: String = "",
    var theme: String = "",
    var tag: String = "",
    var description: String = "",
    var date: Timestamp? = null,
    var schedule: String? = null,
    var place: String? = null,
    var cover_img: String? = null,
    var observation: String? = null,
    var street: String? = null,
    var street_number: String? = null,
    var neighborhood: String? = null,
    var city: String? = null,
    var state: String? = null,
    var state_short: String? = null,
    var country: String? = null,
    var postal_code: String? = null,
    var address: String? = null,
    var lat: Double? = null,
    var long: Double? = null,
    var moderator_1: User? = null,
    var moderator_2: User? = null,
    var embassy_id: String? = null,
    var embassy: Embassy? = null
) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "theme" to theme,
            "tag" to tag,
            "description" to description,
            "date" to date,
            "schedule" to schedule,
            "place" to place,
            "cover_img" to cover_img,
            "observation" to observation,
            "street" to street,
            "street_number" to street_number,
            "neighborhood" to neighborhood,
            "city" to city,
            "state" to state,
            "state_short" to state_short,
            "country" to country,
            "postal_code" to postal_code,
            "address" to address,
            "lat" to lat,
            "long" to long,
            "moderator_1" to moderator_1?.toBasicMap(),
            "moderator_2" to moderator_2?.toBasicMap(),
            "embassy" to embassy?.toBasicMap()
        )
    }
    @Exclude
    fun toBasicMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "theme" to theme,
            "cover_img" to cover_img,
            "city" to city,
            "state_short" to state_short,
            "embassy" to embassy?.toBasicMap()
        )
    }
}