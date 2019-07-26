package com.balloondigital.egvapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var dbListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btMenuEditProfile) {
            startEditProfileActivity()
        }

        if(id == R.id.btMenuChangePic) {
            startChangeProfilePhotoActivity()
        }

        if(id == R.id.btMenuChangePass) {
            startChangePassActivity()
        }

        if(id == R.id.btMenuEditSocial) {
            startEditSocialActivity()
        }

        if(id == R.id.btMenuMyEmbassy) {
            startMyEmbassyActivity()
        }

        if(id == R.id.btMenuManageEvents) {
            startEnrolledEventsActivity()
        }

        if(id == R.id.btMenuMyEvents) {
            startFavoriteEventsActivity()
        }

        if(id == R.id.btMenuManagePrivacy) {
            startSetPrivacyActivity()
        }

        if(id == R.id.btMenuPrivacy) {
            startPrivacyActivity()
        }

        if(id == R.id.btMenuAboutEmbassies) {
            startAboutEmbassiesActivity()
        }

        if(id == R.id.btMenuAboutApp) {
            startAboutAppActivity()
        }

        if(id == R.id.btMenuSuggests) {
            startSuggestsActivity()
        }

        if(id == R.id.btMenuRate) {
            startAppRateActivity()
        }

        if(id == R.id.btMenuSendMenssages) {
            startSendMessageActivity()
        }

        if(id == R.id.btMenuLogout) {
            mAuth.signOut()
            startCheckAuthActivity()
            finish()
        }
    }

    fun setListeners() {
        btMenuEditProfile.setOnClickListener(this)
        btMenuChangePic.setOnClickListener(this)
        btMenuChangePass.setOnClickListener(this)
        btMenuEditSocial.setOnClickListener(this)
        btMenuMyEmbassy.setOnClickListener(this)
        btMenuManageEvents.setOnClickListener(this)
        btMenuMyEvents.setOnClickListener(this)
        btMenuManagePrivacy.setOnClickListener(this)
        btMenuPrivacy.setOnClickListener(this)
        btMenuAboutEmbassies.setOnClickListener(this)
        btMenuAboutApp.setOnClickListener(this)
        btMenuSuggests.setOnClickListener(this)
        btMenuRate.setOnClickListener(this)
        btMenuSendMenssages.setOnClickListener(this)
        btMenuLogout.setOnClickListener(this)

        dbListener = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot != null) {
                    val user: User? = documentSnapshot.toObject(User::class.java)
                    if(user != null) {
                        mUser = user
                        Log.d("FirebaseLog", "ListenerOn")
                    }
                }
            }
    }

    fun startCheckAuthActivity() {
        val intent: Intent = Intent(this, CheckAuthActivity::class.java)
        startActivity(intent)
    }

    fun startEditProfileActivity() {
        val intent: Intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    fun startChangeProfilePhotoActivity() {
        val intent: Intent = Intent(this, ChangeProfilePhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    fun startChangePassActivity() {
        val intent: Intent = Intent(this, ChangePassActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    fun startEditSocialActivity() {
        val intent: Intent = Intent(this, EditSocialActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    fun startMyEmbassyActivity() {
        val intent: Intent = Intent(this, MyEmbassyActivity::class.java)
        startActivity(intent)
    }

    fun startSetPrivacyActivity() {
        val intent: Intent = Intent(this, SetPrivacyActivity::class.java)
        startActivity(intent)
    }

    fun startPrivacyActivity() {
        val intent: Intent = Intent(this, PrivacyActivity::class.java)
        startActivity(intent)
    }

    fun startEnrolledEventsActivity() {
        val intent: Intent = Intent(this, EnrolledEventsActivity::class.java)
        startActivity(intent)
    }

    fun startFavoriteEventsActivity() {
        val intent: Intent = Intent(this, FavoriteEventsActivity::class.java)
        startActivity(intent)
    }

    fun startAboutEmbassiesActivity() {
        val intent: Intent = Intent(this, AboutEmbassiesActivity::class.java)
        startActivity(intent)
    }

    fun startAboutAppActivity() {
        val intent: Intent = Intent(this, AboutAppActivity::class.java)
        startActivity(intent)
    }

    fun startSuggestsActivity() {
        val intent: Intent = Intent(this, SuggestsActivity::class.java)
        startActivity(intent)
    }

    fun startAppRateActivity() {
        val intent: Intent = Intent(this, AppRateActivity::class.java)
        startActivity(intent)
    }

    fun startSendMessageActivity() {
        val intent: Intent = Intent(this, SendMessageActivity::class.java)
        startActivity(intent)
    }
}
