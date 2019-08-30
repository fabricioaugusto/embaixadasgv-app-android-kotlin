package com.balloondigital.egvapp.activity.Edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.PermissionConfig
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_change_profile_photo.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class ChangeProfilePhotoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private val GALLERY_CODE: Int = 200
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile_photo)

        supportActionBar!!.title = "Alterar foto de perfil"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        setListeners()
        getUserDetails()
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

    fun getUserDetails() {

        if(mUser.profile_img != null) {
            Glide.with(this)
                .load(mUser.profile_img)
                .into(imgChangeProfilePhoto)
        } else {
            if(mUser.gender == "Female") {
                imgChangeProfilePhoto.setImageResource(R.drawable.avatar_woman)
            }
        }
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
                                .setAspectRatio(1, 1)
                                .setFixAspectRatio(true)
                                .start(this)
                        }
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                        if (data != null) {
                            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                            imgChangeProfilePhoto.setImageURI(result.uri)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(view: View) {
        val id = view.id

        if (id == R.id.btSaveChangeProfilePhoto) {
            saveUserData()
        }

        if (id == R.id.btChangeProfilePhoto || id == R.id.imgChangeProfilePhoto) {
            startGalleryActivity()
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("user", mUser)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun startGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun setListeners() {
        btSaveChangeProfilePhoto.setOnClickListener(this)
        btChangeProfilePhoto.setOnClickListener(this)
        imgChangeProfilePhoto.setOnClickListener(this)
    }

    private fun saveUserData() {

        btSaveChangeProfilePhoto.startAnimation()

        val imageName = UUID.randomUUID().toString()
        val fileName: String = "$imageName.jpg"

        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.USER_PROFILE}/$fileName")
        mUser.profile_img_file_name = fileName

        val bitmap = (imgChangeProfilePhoto.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Erro ao carregar a imagem. Tente novamente!", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {

            val metadata = it.metadata
            if (metadata != null) {
                val ref = metadata.reference
                if (ref != null) {
                    ref.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                        val uri = task.result
                        if (uri != null) {
                            mUser.profile_img = uri.toString()
                            mDatabase.collection(mCollections.USERS)
                                .document(mUser.id)
                                .set(mUser.toMap())
                                .addOnSuccessListener {
                                    UserService.updateUserDocuments(mDatabase, mUser, mDatabase.batch())
                                    btSaveChangeProfilePhoto.doneLoadingAnimation(
                                        resources.getColor(R.color.colorGreen),
                                        Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                                    )
                                }
                        }
                    })
                }
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

}
