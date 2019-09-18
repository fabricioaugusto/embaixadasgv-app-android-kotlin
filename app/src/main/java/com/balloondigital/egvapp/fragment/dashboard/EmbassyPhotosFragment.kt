package com.balloondigital.egvapp.fragment.dashboard


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Menu.SendEmbassyPhotoActivity
import com.balloondigital.egvapp.activity.Single.SinglePhotoActivity
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.balloondigital.egvapp.model.User
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.activity_embassy_photos.gvEmbassyPhotos
import kotlinx.android.synthetic.main.activity_embassy_photos.swipeLayoutEmbassyPhotos
import kotlinx.android.synthetic.main.fragment_embassy_photos.*
import kotlinx.android.synthetic.main.fragment_single_post.btBackPress

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EmbassyPhotosFragment : Fragment(), View.OnClickListener {

    private lateinit var mEmbassyID: String
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: GridPhotosAdapter
    private lateinit var mPhotoList: MutableList<String>
    private lateinit var mEmbassyPhoto: EmbassyPhoto
    private lateinit var mEmbassyPhotoList: MutableList<EmbassyPhoto>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_embassy_photos, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
        }

        mToolbar = view.findViewById(R.id.embassyPhotosToolbar)
        mToolbar.title = ""


        mContext = view.context

        mDatabase = MyFirebase.database()

        mPhotoList = mutableListOf()
        mEmbassyPhotoList = mutableListOf()

        val config: ImageLoaderConfiguration = ImageLoaderConfiguration
            .Builder(mContext)
            .memoryCache(LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .diskCacheSize(50 * 1024 * 1024)
            .diskCacheFileCount(100)
            .build()
        ImageLoader.getInstance().init(config)

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btBackPress.setOnClickListener(this)

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
                    layoutEmptyPost.isGone = true
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

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    private fun setGridView() {

        mPhotoList.clear()
        mEmbassyPhotoList.clear()

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY_PHOTOS)
            .whereEqualTo("embassy_id", mEmbassyID)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                if(querySnapshot.size() > 0) {
                    for(document in querySnapshot.documents) {
                        val embassyPhoto = document.toObject(EmbassyPhoto::class.java)
                        if(embassyPhoto != null) {
                            mPhotoList.add(embassyPhoto.picture.toString())
                            mEmbassyPhotoList.add(embassyPhoto)
                        }
                    }
                    layoutEmptyPost.isGone = true
                } else {
                    layoutEmptyPost.isGone = false
                }
                mAdapter = GridPhotosAdapter(mContext, R.layout.adapter_grid_photo, mPhotoList)
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
        val intent: Intent = Intent(mContext, SinglePhotoActivity::class.java)
        intent.putExtra("photoUrl", photoUrl)
        startActivity(intent)
    }

}
