package com.balloondigital.egvapp.activity.Create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.internal.NavigationMenu
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import io.github.yavski.fabspeeddial.FabSpeedDial
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CreatePostActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mPost: Post
    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private val GALLERY_CODE: Int = 200
    private var mImgPostIsSet = false
    private var mPublishPostIsHide = true
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        supportActionBar!!.title = "Nova Publicação"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        mPost = Post()
        mPost.type = "post"
        mPost.user_id = mUser.id
        mPost.user = mUser

        setListeners()
    }

    private fun setListeners() {

        layoutAlertInfo.setOnClickListener(this)
        imgPostInsertPic.setOnClickListener(this)
        btPostInsertPic.setOnClickListener(this)
        btPostPublish.setOnClickListener(this)

        val fabListener = object : FabSpeedDial.MenuListener {
            override fun onPrepareMenu(p0: NavigationMenu?): Boolean {
                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.fabArticlePublish -> showPublishPost()
                }
                return true
            }
            override fun onMenuClosed() {}
        }

        fabSpeedDial.setMenuListener(fabListener)
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.imgPostInsertPic) {
            startGalleryActivity()
        }

        if(id == R.id.btPostInsertPic) {
            startGalleryActivity()
        }

        if(id == R.id.btPostPublish) {
            saveUserData()
        }

        if(id == R.id.layoutAlertInfo) {
            layoutAlertInfo.animate().alpha(0F).withEndAction {
                layoutAlertInfo.isGone = true
            }
        }
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

    private fun startGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            try {
                when (requestCode) {
                    GALLERY_CODE -> {
                        if (data != null) {
                            val uri = data.data
                            Log.d("GalleryActivity", "Chegou aqui")
                            CropImages.postPicture(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                imgPostInsertPic.setImageURI(resultUri)
                            }
                            mImgPostIsSet = true
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showPublishPost() {
        mPublishPostIsHide = false
        layoutToughtPublish.isGone = false
        layoutToughtModal.isGone = false
        layoutToughtModal.animate().alpha(1.0F)
    }

    private fun hidePublishPost() {
        mPublishPostIsHide = true
        layoutToughtModal.animate().alpha(0F)
        layoutToughtPublish.isGone = true
        layoutToughtModal.isGone = true
    }

    private fun saveUserData() {

        val description = etPostDescription.text.toString()

        if(description.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        if(!mImgPostIsSet) {
            makeToast("Você deve escolher uma imagem de capa")
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

        mPost.text = description
        mPost.embassy_id = mUser.embassy_id

        btPostPublish.startAnimation()

        val imageName = UUID.randomUUID().toString()
        val fileName = "$imageName.jpg"
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.POST_IMG}/$imageName.jpg")
        mPost.picture_file_name = fileName

        val bitmap = (imgPostInsertPic.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Erro ao carregar a imagem. Tente novamente!", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { it ->

            val metadata = it.metadata
            if (metadata != null) {
                val ref = metadata.reference
                if (ref != null) {
                    ref.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                        val uri = task.result
                        if (uri != null) {
                            mPost.picture = uri.toString()
                            mDatabase.collection(mCollections.POSTS)
                                .add(mPost.toMapPost())
                                .addOnSuccessListener { document ->

                                    document.update("id", document.id).addOnSuccessListener {
                                        btPostPublish.doneLoadingAnimation(
                                            resources.getColor(R.color.colorGreen),
                                            Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                                        ).apply {
                                            setResult(Activity.RESULT_OK)
                                            finish()
                                        }

                                    }
                                }
                        }
                    })
                }
            }

            Toast.makeText(this, "Imagem carregada com sucesso!", Toast.LENGTH_LONG).show()
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
