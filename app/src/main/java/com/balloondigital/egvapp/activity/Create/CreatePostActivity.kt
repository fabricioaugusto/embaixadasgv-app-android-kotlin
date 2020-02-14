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
import androidx.core.net.toUri
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
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
    private var mTextCheck = true
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
        mPost.user_id = mUser.id
        mPost.user = mUser

        setListeners()
        bindData()
    }

    private fun setListeners() {

        imgPostInsertPic.setOnClickListener(this)
        btPostPublish.setOnClickListener(this)
        btPostAddPicture.setOnClickListener(this)
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.btPostAddPicture) {
            startGalleryActivity()
        }

        if(id == R.id.imgPostInsertPic) {
            startGalleryActivity()
        }

        if(id == R.id.btPostPublish) {
            prepareData()
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
                            CropImages.postPicture(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                imgPostInsertPic.setImageDrawable(null)
                                imgPostInsertPic.setImageURI(resultUri)
                            }
                            mImgPostIsSet = true
                            imgPostInsertPic.isGone = false
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun bindData() {

        txtPostUserName.text = mUser.name

        val requestOptions: RequestOptions = RequestOptions()
        val options = requestOptions.transforms(CenterCrop(), RoundedCorners(120))

        if(!mUser.profile_img.isNullOrEmpty()) {
            Glide.with(this)
                .load(mUser.profile_img!!.toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(imgPostUserProfile)
        }

        if(mUser.committee_leader) {
            switchPostHighlight.isGone = false
        }
    }

    private fun prepareData() {

        val description = etPostDescription.text.toString()

        if(description.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        mPost.text = description
        mPost.embassy_id = mUser.embassy_id

        if(mUser.influencer || mUser.counselor || switchPostHighlight.isChecked) {
            mPost.user_verified = true
        }

        if(mImgPostIsSet) {
            publishPostPicture()
        } else {
            publishPostThought()
        }

    }


    private fun publishPostPicture() {

        mPost.type = "post"

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
                                            val returnIntent = Intent()
                                            returnIntent.putExtra("post_verified", mPost.user_verified)
                                            setResult(Activity.RESULT_OK, returnIntent)
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

    private fun publishPostThought() {
        mPost.type = "thought"

        btPostPublish.startAnimation()

        while (mTextCheck) {

            val lastStr = mPost.text.toString().takeLast(4)

            if(lastStr == "<br>") {
                mPost.text = mPost.text.toString().dropLast(4)
            } else {
                mTextCheck = false
            }
        }

        mDatabase.collection(mCollections.POSTS)
            .add(mPost.toMapTought())
            .addOnSuccessListener { document ->

                document.update("id", document.id).addOnSuccessListener {
                    btPostPublish.doneLoadingAnimation(
                        resources.getColor(R.color.colorGreen),
                        Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                    ).apply {
                        val returnIntent = Intent()
                        returnIntent.putExtra("post_verified", mPost.user_verified)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
