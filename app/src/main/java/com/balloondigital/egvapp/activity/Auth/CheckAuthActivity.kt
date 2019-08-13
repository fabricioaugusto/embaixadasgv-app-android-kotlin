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
import com.balloondigital.egvapp.activity.Edit.CompleteRegisterActivity
import com.balloondigital.egvapp.activity.MainActivity


class CheckAuthActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUser: User

    override fun onStart() {
        super.onStart()

        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyDu9n938_SYxGcdZQx5hLC91vFa-wf-JoY")
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
