package com.balloondigital.egvapp.api

import android.content.Context
import android.content.SharedPreferences
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserService private constructor(){
    companion object {
        fun authUserID(context: Context): String {
            val sharedPref: SharedPreferences = context.getSharedPreferences(APP_STORAGE.shared, Context.MODE_PRIVATE)
            return sharedPref.getString(APP_STORAGE.KEY.auth_uid, "token")!!
        }

        fun authUserDetails(database: FirebaseFirestore, context: Context): User? {

            val document = database.collection(MyFirebase.COLLECTIONS.USERS).document(authUserID(context))
                .get().result

            return document?.toObject(User::class.java)
        }

        fun authCurrentUser(): FirebaseUser? {

            return FirebaseAuth.getInstance().currentUser
        }
    }

    object APP_STORAGE {
        const val shared = "oF0kMyuPKmAH"
        object KEY {
            const val auth_uid = "fb_uid"
        }
    }
}