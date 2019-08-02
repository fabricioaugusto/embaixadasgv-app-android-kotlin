package com.balloondigital.egvapp.activity.Menu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Auth.CheckAuthActivity
import com.balloondigital.egvapp.activity.Edit.ChangePassActivity
import com.balloondigital.egvapp.activity.Edit.ChangeProfilePhotoActivity
import com.balloondigital.egvapp.activity.Edit.EditProfileActivity
import com.balloondigital.egvapp.activity.Edit.EditSocialActivity
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var dbListener: ListenerRegistration
    private val MENU_REQUEST_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        getUserDetails()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MENU_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if(data != null) {
                    mUser = data.getSerializableExtra("user") as User
                    getUserDetails()
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.imgMenuUserProfile
            || id == R.id.txtMenuUserName
            || id == R.id.txtMenuViewProfile) {
            startUserProfileActivity()
        }

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
        imgMenuUserProfile.setOnClickListener(this)
        txtMenuUserName.setOnClickListener(this)
        txtMenuViewProfile.setOnClickListener(this)

        /*dbListener = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot != null) {
                    val user: User? = documentSnapshot.toObject(User::class.java)
                    if(user != null) {
                        mUser = user
                        Log.d("FirebaseLog", "ListenerOn")
                    }
                }
            }*/
    }

    private fun startCheckAuthActivity() {
        val intent: Intent = Intent(this, CheckAuthActivity::class.java)
        startActivity(intent)
    }

    private fun startUserProfileActivity() {
        val intent: Intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra("user", mUser)
        finish()
    }

    private fun startEditProfileActivity() {
        val intent: Intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startChangeProfilePhotoActivity() {
        val intent: Intent = Intent(this, ChangeProfilePhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startChangePassActivity() {
        val intent: Intent = Intent(this, ChangePassActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startEditSocialActivity() {
        val intent: Intent = Intent(this, EditSocialActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startMyEmbassyActivity() {
        val intent: Intent = Intent(this, MyEmbassyActivity::class.java)
        startActivity(intent)
    }

    private fun startSetPrivacyActivity() {
        val intent: Intent = Intent(this, SetPrivacyActivity::class.java)
        startActivity(intent)
    }

    private fun startPrivacyActivity() {
        val intent: Intent = Intent(this, PrivacyActivity::class.java)
        startActivity(intent)
    }

    private fun startEnrolledEventsActivity() {
        val intent: Intent = Intent(this, EnrolledEventsActivity::class.java)
        startActivity(intent)
    }

    private fun startFavoriteEventsActivity() {
        val intent: Intent = Intent(this, FavoriteEventsActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutEmbassiesActivity() {
        val intent: Intent = Intent(this, AboutEmbassiesActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutAppActivity() {
        val intent: Intent = Intent(this, AboutAppActivity::class.java)
        startActivity(intent)
    }

    private fun startSuggestsActivity() {
        val intent: Intent = Intent(this, SuggestsActivity::class.java)
        startActivity(intent)
    }

    private fun startAppRateActivity() {
        val intent: Intent = Intent(this, AppRateActivity::class.java)
        startActivity(intent)
    }

    private fun startSendMessageActivity() {
        val intent: Intent = Intent(this, SendMessageActivity::class.java)
        startActivity(intent)
    }

    private fun getUserDetails() {

        Glide.with(this)
            .load(mUser.profile_img)
            .into(imgMenuUserProfile)

        txtMenuUserName.text = mUser.name
    }
}
