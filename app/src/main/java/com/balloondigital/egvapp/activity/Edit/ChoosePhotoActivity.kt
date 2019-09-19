package com.balloondigital.egvapp.activity.Edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.MainActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_choose_photo.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*


class ChoosePhotoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private var mImageIsLoaded = false
    private val GALLERY_CODE: Int = 200
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_photo)

        val toolbar: androidx.appcompat.widget.Toolbar = toolbarCP
        toolbar.setTitle("Complete seu perfil")
        toolbar.setTitleTextColor(resources.getColor(R.color.colorGrey))
        setSupportActionBar(toolbar)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        setListeners()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            try {
                when (requestCode) {
                    GALLERY_CODE -> {
                        if (data != null) {
                            val uri = data.data
                            CropImages.profilePicture(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                imgCPUserProfile.setImageDrawable(null)
                                imgCPUserProfile.setImageURI(resultUri)
                                mImageIsLoaded = true
                            }
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

        if (id == R.id.btCPSavePhoto) {
            saveUserData()
        }

        if (id == R.id.btCPChoosePhoto || id == R.id.imgCPUserProfile) {
            startGalleryActivity()
        }

        if(id == R.id.layoutAlertInfo) {
            layoutAlertInfo.animate().alpha(0F).withEndAction {
                layoutAlertInfo.isGone = true
            }
        }
    }

    private fun startGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun startMainActivity() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun setListeners() {
        layoutAlertInfo.setOnClickListener(this)
        btCPSavePhoto.setOnClickListener(this)
        btCPChoosePhoto.setOnClickListener(this)
        imgCPUserProfile.setOnClickListener(this)
    }

    private fun saveUserData() {

        if(!mImageIsLoaded) {
            makeToast("Selecione uma imagem de sua biblioteca antes de salvar")
            return
        }

        btCPSavePhoto.startAnimation()

        val imageName = UUID.randomUUID().toString()
        val fileName = "$imageName.jpg"
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.USER_PROFILE}/$imageName.jpg")
        mUser.profile_img_file_name = fileName

        val bitmap = (imgCPUserProfile.drawable as BitmapDrawable).bitmap
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

                                    mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
                                        .document(mUser.embassy_id.toString())
                                        .update("leader", mUser.toBasicMap(),
                                            "status", "active")
                                        .addOnSuccessListener {
                                            btCPSavePhoto.doneLoadingAnimation(
                                                resources.getColor(R.color.colorGreen),
                                                drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                                            )
                                            startMainActivity()
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

    fun drawableToBitmap(drawable: Drawable): Bitmap {

        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            val bitmapDrawable: BitmapDrawable = drawable as BitmapDrawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap;
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap =
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888);
        }

        val canvas: Canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
