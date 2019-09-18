package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class EmbassyPhoto (
    var id: String = "",
    var date: Timestamp? = null,
    var text: String? = null,
    var picture: String = "",
    var picture_file_name: String? = null,
    var thumbnail: String? = null,
    var embassy_id: String? = null
): Serializable {
    @Exclude
    fun toMapNote(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "text" to text,
            "picture" to picture,
            "picture_file_name" to picture_file_name,
            "thumbnail" to thumbnail,
            "embassy_id" to embassy_id
        )
    }
}