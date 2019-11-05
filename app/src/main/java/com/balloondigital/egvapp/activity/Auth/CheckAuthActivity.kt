package com.balloondigital.egvapp.activity.Auth

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.libraries.places.api.Places
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.balloondigital.egvapp.activity.Edit.ChoosePhotoActivity
import com.balloondigital.egvapp.activity.Edit.CompleteRegisterActivity
import com.balloondigital.egvapp.activity.MainActivity
import com.balloondigital.egvapp.api.GoogleAPI
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.EmbassySponsor
import com.balloondigital.egvapp.utils.AppStatus
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException


class CheckAuthActivity : AppCompatActivity() {

    private lateinit var mMessaging: FirebaseMessaging
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
        mMessaging = MyFirebase.messaging()

        if(AppStatus.isConnected(this)) {
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
        } else {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Desconectado!")
                .setContentText("Parece que o seu dispositivo está offline ou com problemas na conexão!")
                .show()
            return
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

                        if(user.last_device_os != "android") {
                            documentSnapshot.reference.update("last_device_os", "android")
                        }

                        if(user.last_device_version != Build.VERSION.SDK_INT.toString()) {
                            documentSnapshot.reference.update("last_device_version", Build.VERSION.SDK_INT.toString())
                        }

                        if(user.last_app_update != "14") {
                            documentSnapshot.reference.update("last_app_update", "14")
                        }

                        val leader = documentSnapshot.data?.get("leader")
                        if(leader != null) {
                            user.leader = documentSnapshot.data?.get("leader") as Boolean
                        }
                        if(user.leader) {
                            mMessaging.subscribeToTopic("egv_topic_leaders")
                        }
                        mMessaging.subscribeToTopic("egv_topic_members").addOnCompleteListener {
                                task ->
                            if(task.isSuccessful) {
                                documentSnapshot.reference.update("topic_subscribed", true)
                            }
                        }

                        if(user.fcm_token.isNullOrEmpty()) {
                            obterToken(documentSnapshot.reference)
                        }

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

    private fun obterToken(doc: DocumentReference) {

        val autorizacao = "25255760166"
        val firebase = "FCM"

        Thread(Runnable {
            try {

                val token = FirebaseInstanceId.getInstance().getToken(autorizacao, firebase)// gerar um novo token
                if(!token.isNullOrEmpty()) {
                    doc.update("fcm_token", token)
                }
            } catch (e: IOException) {
                //caso aconteça algum erro
            }
        }).start()
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

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
