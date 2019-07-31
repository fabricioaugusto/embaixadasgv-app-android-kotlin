package com.balloondigital.egvapp.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MyFirebase private constructor(){

    companion object {

        fun database(): FirebaseFirestore {

            if (database == null) {
                database = FirebaseFirestore.getInstance()
            }
            return database as FirebaseFirestore
        }

        fun auth(): FirebaseAuth {
            if (auth == null) {
                auth = FirebaseAuth.getInstance()
            }
            return auth as FirebaseAuth
        }

        fun storage(): StorageReference {
            if (storage == null) {
                storage = FirebaseStorage.getInstance().reference
            }
            return storage as StorageReference
        }

        private var auth: FirebaseAuth? = null
        private var database: FirebaseFirestore? = null
        private var storage: StorageReference? = null
    }

    object COLLECTIONS {
        val USERS = "users"
        val EMBASSY = "embassy"
        val LOCATIONS = "locations"
        val POSTS = "posts"
    }
}