package com.balloondigital.egvapp.activity.Dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.SinglePhotoActivity
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.activity_embassy_photos.*


class EmbassyPhotosActivity : AppCompatActivity() {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: GridPhotosAdapter
    private lateinit var mPhotoList: MutableList<String>
    private lateinit var mEmbassyPhotoList: MutableList<EmbassyPhoto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embassy_photos)

        supportActionBar!!.title = "Galeria de fotos"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()

        mPhotoList = mutableListOf()
        mEmbassyPhotoList = mutableListOf()

        val config: ImageLoaderConfiguration = ImageLoaderConfiguration
            .Builder(this)
            .memoryCache(LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .diskCacheSize(50 * 1024 * 1024)
            .diskCacheFileCount(100)
            .build()
        ImageLoader.getInstance().init(config)

        setGridView()
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

    private fun setGridView() {

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY_PHOTOS)
            .whereEqualTo("embassy_id", mUser.embassy_id)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                for(document in querySnapshot.documents) {
                    val embassyPhoto = document.toObject(EmbassyPhoto::class.java)
                    if(embassyPhoto != null) {
                        mPhotoList.add(embassyPhoto.picture.toString())
                        mEmbassyPhotoList.add(embassyPhoto)
                    }
                }
                mAdapter = GridPhotosAdapter(this, R.layout.adapter_grid_photo, mPhotoList)
                gvEmbassyPhotos.adapter = mAdapter
            }

        val photoSelectListener = AdapterView.OnItemClickListener{
                adapter, view, pos, posLong ->
            startSinglePhotoActivity(mPhotoList[pos])
        }

        gvEmbassyPhotos.onItemClickListener = photoSelectListener
    }

    private fun startSinglePhotoActivity(photoUrl: String) {
        val intent: Intent = Intent(this, SinglePhotoActivity::class.java)
        intent.putExtra("photoUrl", photoUrl)
        startActivity(intent)
    }

}
