package com.balloondigital.egvapp.activity.Menu

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
import androidx.core.content.ContextCompat
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.EmbassyPhoto
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_send_embassy_photo.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class SendEmbassyPhotoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mEmbassyPhoto: EmbassyPhoto
    private lateinit var mUser: User
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
        setContentView(R.layout.activity_send_embassy_photo)

        supportActionBar!!.title = "Enviar foto da embaixada"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mEmbassyPhoto = EmbassyPhoto()
        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        setListeners()
    }

    private fun setListeners() {
        imgEmbassyInsertPic.setOnClickListener(this)
        btEmbassyInsertPic.setOnClickListener(this)
        btEmbassyPhotoPublish.setOnClickListener(this)
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.imgEmbassyInsertPic) {
            startGalleryActivity()
        }

        if(id == R.id.btEmbassyInsertPic) {
            startGalleryActivity()
        }

        if(id == R.id.btEmbassyPhotoPublish) {
            saveUserData()
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
                            CropImages.embassyPicture(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                imgEmbassyInsertPic.setImageDrawable(null)
                                imgEmbassyInsertPic.setImageURI(resultUri)
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

    private fun saveUserData() {

        val description = etEmbassyDescription.text.toString()

        if(description.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        if(!mImgPostIsSet) {
            makeToast("Você deve escolher uma imagem de capa")
            return
        }

        mEmbassyPhoto.text = description
        mEmbassyPhoto.embassy_id = mUser.embassy_id

        btEmbassyPhotoPublish.startAnimation()

        val imageName = UUID.randomUUID().toString()
        val fileName = "$imageName.jpg"
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.EMBASSY_PHOTO}/$fileName")
        mEmbassyPhoto.picture_file_name = fileName

        val bitmap = (imgEmbassyInsertPic.drawable as BitmapDrawable).bitmap
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
                            mEmbassyPhoto.picture = uri.toString()
                            mDatabase.collection(mCollections.EMBASSY_PHOTOS)
                                .add(mEmbassyPhoto.toMapNote())
                                .addOnSuccessListener { document ->
                                    mEmbassyPhoto.id = document.id
                                    document.update("id", document.id).addOnSuccessListener {
                                        btEmbassyPhotoPublish.doneLoadingAnimation(
                                            resources.getColor(R.color.colorGreen),
                                            Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                                        ).apply {
                                            val returnIntent = Intent()
                                            returnIntent.putExtra("embassyPhoto", mEmbassyPhoto)
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

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
