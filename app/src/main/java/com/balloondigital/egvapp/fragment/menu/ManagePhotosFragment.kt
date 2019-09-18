package com.balloondigital.egvapp.fragment.menu


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Menu.SendEmbassyPhotoActivity
import com.balloondigital.egvapp.activity.Single.SinglePhotoActivity
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.adapter.ManageItemsDialogAdapter
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
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_manage_photos.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ManagePhotosFragment : Fragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var mEmbassyID: String
    private lateinit var mUser: User
    private lateinit var mCurrentPhoto: EmbassyPhoto
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: GridPhotosAdapter
    private lateinit var mPhotoList: MutableList<String>
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mAdapterDialog: ManageItemsDialogAdapter
    private lateinit var mEmbassyPhoto: EmbassyPhoto
    private lateinit var mEmbassyPhotoList: MutableList<EmbassyPhoto>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View =  inflater.inflate(R.layout.fragment_manage_photos, container, false)
        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
            mEmbassyID = bundle.getString("embassyID", "")
        }

        mToolbar = view.findViewById(R.id.embassyPhotosToolbar)
        mToolbar.title = ""


        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mContext = view.context

        mDatabase = MyFirebase.database()

        mPhotoList = mutableListOf()
        mEmbassyPhotoList = mutableListOf()

        val menuList: List<com.balloondigital.egvapp.model.MenuItem> = listOf(
            com.balloondigital.egvapp.model.MenuItem("Visualizar", "item", R.drawable.ic_visibility_black),
            com.balloondigital.egvapp.model.MenuItem("Excluir", "item", R.drawable.ic_delete_black)
        )

        mAdapterDialog = ManageItemsDialogAdapter(mContext, false, 2, menuList)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_embassy_photos_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_add_picture -> {
                startSendEmbassyPhotosActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {
            startSinglePhotoActivity(mCurrentPhoto.picture)
        }

        if(position == 1) {

            if(mEmbassyID == mCurrentPhoto.embassy_id && mUser.leader)   {
                confirmDialog("Deletar Foto",
                    "Tem certeza que deseja remover esta foto?")
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
            mCurrentPhoto = mEmbassyPhotoList[pos]
            setManageItemsDialog()
        }

        gvEmbassyPhotos.onItemClickListener = photoSelectListener
    }

    private fun startSinglePhotoActivity(photoUrl: String) {
        val intent: Intent = Intent(mContext, SinglePhotoActivity::class.java)
        intent.putExtra("photoUrl", photoUrl)
        startActivity(intent)
    }

    private fun startSendEmbassyPhotosActivity() {
        val intent: Intent = Intent(mContext, SendEmbassyPhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, 100)
    }

    private fun setManageItemsDialog() {

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(mContext)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapterDialog
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setPadding(16, 32, 16, 32)
            dialogBuilder.setGravity(Gravity.CENTER)
            mCPDialog = dialogBuilder.create()
            mCPDialog.show()
        }
    }

    private fun confirmDialog(dialogTitle: String, dialogMessage: String) {
        AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Sim") { dialog, which ->
                MyFirebase.database()
                    .collection(MyFirebase.COLLECTIONS.EMBASSY_PHOTOS)
                    .document(mCurrentPhoto.id).delete()
                    .addOnCompleteListener {
                    setGridView()
                }.addOnFailureListener {
                    Log.d("EGVAPPLOG", it.message.toString())
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

}
