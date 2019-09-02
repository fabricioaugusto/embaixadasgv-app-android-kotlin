package com.balloondigital.egvapp.activity.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.libraries.places.api.Places
import android.content.Context
import android.content.SharedPreferences
import com.balloondigital.egvapp.activity.Edit.ChooseEmbassyActivity
import com.balloondigital.egvapp.activity.Edit.ChoosePhotoActivity
import com.balloondigital.egvapp.activity.Edit.CompleteRegisterActivity
import com.balloondigital.egvapp.activity.MainActivity
import com.balloondigital.egvapp.api.GoogleAPI


class CheckAuthActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUser: User

    override fun onStart() {
        super.onStart()

        // Initialize the SDK
        Places.initialize(applicationContext, GoogleAPI.KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_auth)

        mDatabase = MyFirebase.database()
        mAuth = MyFirebase.auth()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            if(uid != null) {
                getCurrentUser(currentUser.uid)
            } else {
                startLoginActivity()
            }
        } else {
            startLoginActivity()
        }
    }

    fun getCurrentUser(uid: String) {
        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
        collection.document(uid)
            .get().addOnSuccessListener {
                    documentSnapshot ->
                if(documentSnapshot != null) {
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null) {
                        mUser = user
                        checkUser()
                    } else {
                        mAuth.signOut()
                        startLoginActivity()
                    }
                }
            }
    }

    fun checkUser() {

        if(mUser.description == null) {
            startCompleteRegisterActivity()
            return
        } else if(mUser.profile_img == null) {
            startChooseProfileImg()
            return
        } else {
            startMainActivity()
            return
        }
    }

    fun startMainActivity() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
        finish()
    }

    fun startChooseProfileImg() {
        val intent: Intent = Intent(this, ChoosePhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
        finish()
    }

    fun startCompleteRegisterActivity() {
        val intent: Intent = Intent(this, CompleteRegisterActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
        finish()
    }

    fun startLoginActivity() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
