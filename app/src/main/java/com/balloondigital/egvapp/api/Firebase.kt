package com.balloondigital.egvapp.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Firebase private constructor(){

    companion object {

        fun database(): DatabaseReference {

            if (database == null) {
                database = FirebaseDatabase.getInstance().getReference()
            }
            return database as DatabaseReference
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
        private var database: DatabaseReference? = null
        private var storage: StorageReference? = null
    }
}