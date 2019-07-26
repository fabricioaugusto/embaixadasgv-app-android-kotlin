package com.balloondigital.egvapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import android.R.attr.apiKey
import android.content.Context
import android.content.SharedPreferences


class CheckAuthActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUser: User

    override fun onStart() {
        super.onStart()

        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyDu9n938_SYxGcdZQx5hLC91vFa-wf-JoY")

        // Create a new Places client instance
        val placesClient = Places.createClient(this)

        mDatabase = MyFirebase.database()
        mAuth = MyFirebase.auth()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {

            val sharedPref: SharedPreferences = getSharedPreferences("oF0kMyuPKmAH", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("fb_uid", currentUser.uid)
            getCurrentUser(currentUser.uid)
        } else {
            startLoginActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_auth)
    }

    fun getCurrentUser(uid: String) {
        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
        collection.document(uid)
            .get().addOnSuccessListener {
                    documentSnapshot ->
                mUser = documentSnapshot.toObject(User::class.java)!!
                checkUser()
            }
    }

    fun checkUser() {

        if(mUser.embassy == null) {
            startChooseEmbassyActivity()
            finish()
            return
        }

        if(mUser.description == null) {
            startCompleteRegisterActivity()
            finish()
            return
        } else {
            startMainActivity()
            finish()
            return
        }
    }

    fun startMainActivity() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    fun startChooseEmbassyActivity() {
        val intent: Intent = Intent(this, ChooseEmbassyActivity::class.java)
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
