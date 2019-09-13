package com.balloondigital.egvapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

data class Bulletin(
    var id: String = "",
    var type: String = "",
    var date: Timestamp? = null,
    var title: String? = null,
    var resume: String? = null,
    var text: String? = null
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "date" to FieldValue.serverTimestamp(),
            "type" to type,
            "title" to title,
            "resume" to resume,
            "text" to text
        )
    }
}