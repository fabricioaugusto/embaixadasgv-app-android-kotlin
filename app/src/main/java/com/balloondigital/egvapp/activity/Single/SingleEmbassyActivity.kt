package com.balloondigital.egvapp.activity.Single

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_embassy.*
import kotlinx.android.synthetic.main.fragment_single_event.*

class SingleEmbassyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mEmbassy: Embassy
    private lateinit var mEmbassyID: String
    private lateinit var mAdapter: GridPhotosAdapter
    private lateinit var mPhotoList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_embassy)

        supportActionBar!!.title = "Minha embaixada"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
        }

        mDatabase = MyFirebase.database()

        mPhotoList = mutableListOf()

        setListeners()
        getEmbassyDetails()
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
        val id = view.id
        if(id == R.id.btEmbassyPhone) {
            mEmbassy.phone?.replace("(", "")
            mEmbassy.phone?.replace(")", "")
            mEmbassy.phone?.replace("-", "")
            openExternalLink("https://wa.me/55${mEmbassy.phone}")
        }

        if(id == R.id.btEmbassyEmail) {
            openExternalLink("mailto:${mEmbassy.email}")
        }

        if(id == R.id.btEmbassyAgenda) {

        }
    }

    private fun setListeners() {
        btEmbassyPhone.setOnClickListener(this)
        btEmbassyEmail.setOnClickListener(this)
        btEmbassyAgenda.setOnClickListener(this)
    }

    private fun getEmbassyDetails() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .document(mEmbassyID)
            .get()
            .addOnSuccessListener {
                documentSnapshot ->
                val embassy = documentSnapshot.toObject(Embassy::class.java)
                if(embassy != null) {
                    mEmbassy = embassy
                    bindData()
                }
            }
    }

    private fun bindData() {
        txtEmbassyName.text = mEmbassy.name
        txtEmbassyCity.text = "${mEmbassy.city} - ${mEmbassy.state_short}"
        txtLeaderName.text = mEmbassy.leader?.name

        if(mEmbassy.leader?.occupation != null) {
            txtLeaderProfession.text = mEmbassy.leader?.occupation
        } else {
            txtLeaderProfession.text = "Eu sou GV!"
        }

        if(mEmbassy.cover_img != null) {
            Glide.with(this)
                .load(mEmbassy.cover_img)
                .into(imgEmbassyCover)
        }

        Glide.with(this)
            .load(mEmbassy.leader?.profile_img)
            .into(imgEmbassyLeader)

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY_PHOTOS)
            .whereEqualTo("embassy_id", mEmbassyID)
            .limit(3)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                for(document in querySnapshot.documents) {
                    val embassyPhoto = document.toObject(EmbassyPhoto::class.java)
                    if(embassyPhoto != null) {
                        mPhotoList.add(embassyPhoto.picture.toString())
                    }
                }
                mAdapter = GridPhotosAdapter(this, R.layout.adapter_grid_photo, mPhotoList)
                mAdapter.hasStableIds()
                gridEmbassyPhotos.adapter = mAdapter
            }
    }

    private fun openExternalLink(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
