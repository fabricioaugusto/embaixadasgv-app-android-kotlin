package com.balloondigital.egvapp.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch

class UserService private constructor(){
    companion object {
        fun authUserID(context: Context): String {
            val sharedPref: SharedPreferences = context.getSharedPreferences(APP_STORAGE.shared, Context.MODE_PRIVATE)
            return sharedPref.getString(APP_STORAGE.KEY.auth_uid, "token")!!
        }

        fun authUserDetails(database: FirebaseFirestore, context: Context): User? {

            val document = database.collection(MyFirebase.COLLECTIONS.USERS).document(authUserID(context))
                .get().result ?: return null

            return document.toObject(User::class.java)
        }

        fun authCurrentUser(): FirebaseUser? {

            return FirebaseAuth.getInstance().currentUser
        }

        fun updateUserDocuments(database: FirebaseFirestore, user: User, batch: WriteBatch) {
            updateUserLikes(database, user, batch)
        }

        private fun updateUserLikes(database: FirebaseFirestore, user: User, batch: WriteBatch) {

            database.collection(MyFirebase.COLLECTIONS.POST_LIKES)
                .whereEqualTo("user_id", user.id)
                .get()
                .addOnSuccessListener { query ->
                    for(document in query.documents) {
                        batch.update(document.reference, "user", user.toBasicMap())
                    }
                    updateUserComments(database, user, batch)
                }
        }

        private fun updateUserComments(database: FirebaseFirestore, user: User, batch: WriteBatch) {

            database.collection(MyFirebase.COLLECTIONS.POST_COMMENTS)
                .whereEqualTo("user_id", user.id)
                .get()
                .addOnSuccessListener { query ->
                    for(document in query.documents) {
                        batch.update(document.reference, "user", user.toBasicMap())
                    }
                    updatePosts(database, user, batch)
                }
        }

        private fun updatePosts(database: FirebaseFirestore, user: User, batch: WriteBatch) {

            database.collection(MyFirebase.COLLECTIONS.POSTS)
                .whereEqualTo("user_id", user.id)
                .get()
                .addOnSuccessListener { query ->
                    for(document in query.documents) {
                        batch.update(document.reference, "user", user.toBasicMap())
                    }
                    updateEnrollments(database, user, batch)
                }
        }

        private fun updateEnrollments(database: FirebaseFirestore, user: User, batch: WriteBatch) {

            database.collection(MyFirebase.COLLECTIONS.ENROLLMENTS)
                .whereEqualTo("user_id", user.id)
                .get()
                .addOnSuccessListener { query ->
                    for(document in query.documents) {
                        batch.update(document.reference, "user", user.toBasicMap())
                    }
                    batch.commit()
                    Log.d("EGVAPPLOG", "Atualizado todos os documentos")
                }
        }
    }

    object APP_STORAGE {
        const val shared = "oF0kMyuPKmAH"
        object KEY {
            const val auth_uid = "fb_uid"
        }
    }
}