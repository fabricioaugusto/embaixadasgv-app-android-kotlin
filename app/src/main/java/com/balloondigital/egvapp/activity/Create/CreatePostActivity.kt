package com.balloondigital.egvapp.activity.Create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.BasicUser
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CreatePostActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mPost: Post
    private lateinit var mUser: BasicUser
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private val GALLERY_CODE: Int = 200
    private var mImgPostIsSet = false
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        supportActionBar!!.title = "Nova Nota"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as BasicUser
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        mPost = Post()
        mPost.type = "post"
        mPost.user = mUser

        setListeners()
    }

    private fun setListeners() {
        imgPostInsertPic.setOnClickListener(this)
        btPostInsertPic.setOnClickListener(this)
        btPostPublish.setOnClickListener(this)
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
                            CropImage.activity(uri)
                                .setAspectRatio(4, 3)
                                .setFixAspectRatio(true)
                                .start(this)
                        }
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                        if (data != null) {
                            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                            imgPostInsertPic.setImageURI(result.uri)
                            mImgPostIsSet = true
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        val currentDate = sdf.format(Date())

        mPost.text = description

        btPostPublish.startAnimation()

        val imageName = UUID.randomUUID().toString()
        val storagePath: StorageReference = mStorage.child("images/post/article/$imageName.jpg")

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
