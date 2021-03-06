package com.balloondigital.egvapp.activity.Edit

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
import com.balloondigital.egvapp.adapter.GridPhotosAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_edit_embassy.*
import kotlinx.android.synthetic.main.activity_send_embassy_photo.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class EditEmbassyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mEmbassy: Embassy
    private lateinit var mEmbassyID: String
    private lateinit var mStorage: StorageReference
    private lateinit var mPhotoList: MutableList<String>
    private lateinit var mEmbassyFrequency: String
    private var isCoverChanged = false
    private val GALLERY_CODE: Int = 200
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_embassy)

        supportActionBar!!.title = "Minhas embaixadas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
        }

        mDatabase = MyFirebase.database()
        mPhotoList = mutableListOf()
        mStorage = MyFirebase.storage()
        mEmbassyFrequency = ""

        setListeners()
        getEmbassyDetails()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            try {
                when (requestCode) {
                    GALLERY_CODE -> {
                        if (data != null) {
                            val uri = data.data
                            CropImages.embassyCover(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                isCoverChanged = true
                                imgEditEmbassyCover.setImageDrawable(null)
                                imgEditEmbassyCover.setImageURI(resultUri)
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
        if(id == R.id.btEditEmbassySava) {
            saveData()
        }

        if(id == R.id.imgEditEmbassyCover) {
            startGalleryActivity()
        }

        if(id == R.id.layoutAlertInfo) {
            layoutAlertInfo.animate().alpha(0F).withEndAction {
                layoutAlertInfo.isGone = true
            }
        }
    }

    private fun setListeners() {
        layoutAlertInfo.setOnClickListener(this)
        btEditEmbassySava.setOnClickListener(this)
        imgEditEmbassyCover.setOnClickListener(this)
    }

    private fun getEmbassyDetails() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .document(mEmbassyID)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val embassy = documentSnapshot.toObject(Embassy::class.java)
                if(embassy != null) {
                    mEmbassy = embassy
                    bindData()
                }
            }
    }

    private fun bindData() {
        etEditEmbassyName.setText(mEmbassy.name)
        etEditEmbassyPhone.setText(mEmbassy.phone)
        etEditEmbassyEmail.setText(mEmbassy.email)
        etEditEmbassyMembersQuantity.setText(mEmbassy.members_quantity.toString())

        val coverImg = mEmbassy.cover_img

        if(coverImg !=  null) {
            Glide.with(this)
                .load(coverImg)
                .into(imgEditEmbassyCover)
        }

        val frequency = mEmbassy.frequency

        if(frequency != null) {
            mEmbassyFrequency = frequency
        }

        when {
            mEmbassy.frequency == "biweekly" -> {radioBtBiweekly.isChecked = true}
            mEmbassy.frequency == "monthly" -> {radioBtMonthly.isChecked = true}
            mEmbassy.frequency == "weekly" -> {radioBtWeekly.isChecked = true}
        }

    }

    private fun startGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun saveData() {

        val name = etEditEmbassyName.text.toString()
        val email = etEditEmbassyEmail.text.toString()
        val phone = etEditEmbassyPhone.text.toString()
        val embassyQuantity = etEditEmbassyMembersQuantity.text.toString().toInt()


        if(radioBtBiweekly.isChecked) {
            mEmbassy.frequency = "biweekly"
        }

        if(radioBtMonthly.isChecked) {
            mEmbassy.frequency = "monthly"
        }

        if(radioBtWeekly.isChecked) {
            mEmbassy.frequency = "weekly"
        }

        if(name.isEmpty()) {
            makeToast("O campo de Nome da embaixada não pode ficar vazio")
            return
        }


        if(name == mEmbassy.name && email == mEmbassy.email && phone == mEmbassy.phone && !isCoverChanged && embassyQuantity == mEmbassy.members_quantity && mEmbassyFrequency == mEmbassy.frequency) {
            makeToast("Nenhuma alteração foi realizada")
            return
        }

        mEmbassy.name = name
        mEmbassy.email = email
        mEmbassy.phone = phone
        mEmbassy.members_quantity = embassyQuantity

        btEditEmbassySava.startAnimation()

        if(!isCoverChanged) {
            mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
                .document(mEmbassyID)
                .set(mEmbassy.toMap())
                .addOnSuccessListener {
                    btEditEmbassySava.doneLoadingAnimation(
                        resources.getColor(com.balloondigital.egvapp.R.color.colorGreen),
                        Converters.drawableToBitmap(resources.getDrawable(com.balloondigital.egvapp.R.drawable.ic_check_grey_light))
                    )
                }
        }


        var fileName: String? = mEmbassy.cover_img_file_name

        if(fileName.isNullOrEmpty()) {
            val imageName = UUID.randomUUID().toString()
            fileName = "$imageName.jpg"
        }

        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.EMBASSY_COVER}/$fileName")
        mEmbassy.cover_img_file_name = fileName

        val bitmap = (imgEditEmbassyCover.drawable as BitmapDrawable).bitmap
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
                            mEmbassy.cover_img = uri.toString()
                            mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
                                .document(mEmbassyID)
                                .set(mEmbassy.toMap())
                                .addOnSuccessListener {
                                    btEditEmbassySava.doneLoadingAnimation(
                                        resources.getColor(com.balloondigital.egvapp.R.color.colorGreen),
                                        Converters.drawableToBitmap(resources.getDrawable(com.balloondigital.egvapp.R.drawable.ic_check_grey_light))
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
