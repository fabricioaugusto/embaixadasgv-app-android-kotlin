package com.balloondigital.egvapp.activity.Single

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity(), View.OnClickListener {

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
        setListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> true
        }
    }

    override fun onClick(view: View) {

        when(view.id) {
            R.id.btUserProfileWa -> openExternalLink(mUser.whatsapp!!)
            R.id.btUserProfileFb -> openExternalLink("https://facebook.com/${mUser.facebook!!}")
            R.id.btUserProfileInsta -> openExternalLink("https://www.instagram.com/${mUser.instagram!!}")
            R.id.btUserProfileIn -> openExternalLink("https://www.linkedin.com/in/${mUser.linkedin!!}")
            R.id.btUserProfileTt -> openExternalLink("https://twitter.com/${mUser.twitter!!}")
            R.id.btUserProfileYt -> openExternalLink(mUser.youtube!!)
            R.id.btUserProfileGit -> openExternalLink("https://github.com/${mUser.github!!}")
            R.id.btUserProfileBh -> openExternalLink("https://www.behance.net/${mUser.behance!!}")
        }
    }

    private fun setListeners() {
        btUserProfileMail.setOnClickListener(this)
        btUserProfileWa.setOnClickListener(this)
        btUserProfileFb.setOnClickListener(this)
        btUserProfileInsta.setOnClickListener(this)
        btUserProfileIn.setOnClickListener(this)
        btUserProfileGit.setOnClickListener(this)
        btUserProfileBh.setOnClickListener(this)
        btUserProfileTt.setOnClickListener(this)
        btUserProfileYt.setOnClickListener(this)
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
        txtUserProfileEmbassy.text = mUser.embassy?.name

        getSocialData()
    }

    private fun getSocialData() {

        if(mUser.facebook != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileFb.isVisible = true
        }

        if(mUser.whatsapp != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileWa.isVisible = true
        }

        if(mUser.instagram != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileInsta.isVisible = true
        }

        if(mUser.linkedin != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileIn.isVisible = true
        }

        if(mUser.twitter != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileTt.isVisible = true
        }

        if(mUser.youtube != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileYt.isVisible = true
        }

        if(mUser.github != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileGit.isVisible = true
        }

        if(mUser.behance != null) {
            if(layoutSocialBts.isGone) {
                layoutSocialBts.isVisible = true
            }
            btUserProfileBh.isVisible = true
        }
    }

    private fun openExternalLink(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
