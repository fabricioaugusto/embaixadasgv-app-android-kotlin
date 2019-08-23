package com.balloondigital.egvapp.activity.Single

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_embassy.*

class SingleEmbassyActivity : AppCompatActivity() {


    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mEmbassy: Embassy
    private lateinit var mEmbassyID: String
    private lateinit var mAdapter: GridPhotosAdapter
    private lateinit var mPhotoList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_embassy)

        mDatabase = MyFirebase.database()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
        }

        mPhotoList = mutableListOf()
        supportActionBar!!.title = "Minhas embaixadas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
}
