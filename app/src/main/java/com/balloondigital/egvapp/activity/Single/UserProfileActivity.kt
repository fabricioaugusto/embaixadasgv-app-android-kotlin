package com.balloondigital.egvapp.activity.Single

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var mUser: User
    private lateinit var mEmbassy: Embassy
    private lateinit var mDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        supportActionBar!!.title = mUser.name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()

        getUserDetails()
    }

    fun getUserDetails() {

        Glide
            .with(this)
            .load(mUser.profile_img)
            .into(imgUserProfile)

        val city = "${mUser.city}, ${mUser.state_short}"

        txtUserProfileName.text = mUser.name
        txtUserProfileOccupation.text = mUser.occupation
        txtUserProfileCity.text = city
        txtUserProfileBiography.text = mUser.description

        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
        collection.document(mUser.embassy!!)
            .get().addOnSuccessListener {
                    documentSnapshot ->
                mEmbassy = documentSnapshot.toObject(Embassy::class.java)!!
                txtUserProfileEmbassy.text = mEmbassy.name
            }
    }
}
