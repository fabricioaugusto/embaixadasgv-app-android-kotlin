package com.balloondigital.egvapp.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
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

        fun messaging(): FirebaseMessaging {
            if (messaging == null) {
                messaging = FirebaseMessaging.getInstance()
            }
            return messaging as FirebaseMessaging
        }

        private var auth: FirebaseAuth? = null
        private var database: FirebaseFirestore? = null
        private var storage: StorageReference? = null
        private var messaging: FirebaseMessaging? = null
    }

    object COLLECTIONS {
        val USERS = "users"
        val EVENTS = "events"
        val ENROLLMENTS = "enrollments"
        val EMBASSY = "embassy"
        val BULLETIN = "bulletins"
        val COMMITTEES = "committees"
        val INTERESTED = "interested"
        val NOTIFICATIONS = "notifications"
        val EMBASSY_PHOTOS = "embassy_photos"
        val LOCATIONS = "locations"
        val POSTS = "posts"
        val POST_LIKES = "post_likes"
        val POST_COMMENTS = "post_comments"
        val SPONSORS = "sponsors"
        val APP_INVITATIONS = "app_invitations"
        val APP_MESSAGES = "app_messages"
        val APP_SERVER = "server_data"
        val INVITATION_REQUEST = "invitation_request"
        object APP_CONTENT {
            val name = "app_content"
            object KEYS {
                val ABOUT_EMBASSY = "about_embassy"
            }
        }
    }

    object STORAGE {
        val USER_PROFILE = "images/user/profile"
        val POST_IMG = "images/post/article"
        val EVENT_COVER = "images/event/cover"
        val EVENT_MODERATOR_PHOTO = "images/event/moderator"
        val EMBASSY_PHOTO = "images/embassy/picture"
        val EMBASSY_COVER = "images/embassy/cover"
    }
}