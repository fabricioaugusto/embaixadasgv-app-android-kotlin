package com.balloondigital.egvapp.activity.Dashboard

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Menu.SendEmbassyPhotoActivity
import com.balloondigital.egvapp.activity.Single.SinglePhotoActivity
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.adapter.MenuListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.balloondigital.egvapp.model.User
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
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
    private lateinit var mEmbassyPhoto: EmbassyPhoto
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

        swipeLayoutEmbassyPhotos.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            setGridView()
        })

        setGridView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                if(data != null) {
                    mEmbassyPhoto = data.getSerializableExtra("embassyPhoto") as EmbassyPhoto
                    mPhotoList.add(0, mEmbassyPhoto.picture.toString())
                    mEmbassyPhotoList.add(0, mEmbassyPhoto)
                    mAdapter.notifyDataSetChanged()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_embassy_photos_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.bar_add_picture -> {
                startSendEmbassyPhotosActivity()
                return true
            }
            else -> true
        }
    }

    private fun setGridView() {

        mPhotoList.clear()

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
                mAdapter.hasStableIds()
                gvEmbassyPhotos.adapter = mAdapter
                swipeLayoutEmbassyPhotos.isRefreshing = false
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

    private fun startSendEmbassyPhotosActivity() {
        val intent: Intent = Intent(this, SendEmbassyPhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, 100)
    }
}
