package com.balloondigital.egvapp.activity.Create

import android.Manifest
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.activity.Edit.TextEditorActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.CropImages
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_create_article.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class CreateArticleActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var mPost: Post
    private lateinit var mUser: User
    private lateinit var mKnifeText: String
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private var mCoverIsSet = false
    private val GALLERY_CODE: Int = 200
    private val KNIFE_TEXT = 500
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

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
        mPost.type = "note"
        mPost.user_id = mUser.id
        mPost.user = mUser

        mKnifeText = ""

        setListeners()
    }

    private fun setListeners() {
        imgArticleInsertCover.setOnClickListener(this)
        btArticlePublish.setOnClickListener(this)
        txtOpenKinfe.setOnClickListener(this)
    }

    override fun onClick(view: View) {

        val id = view.id


        if(id == R.id.imgArticleInsertCover) {
            startGalleryActivity()
        }

        if(id == R.id.txtOpenKinfe) {
            startTextEditor()
        }

        if(id == R.id.btArticlePublish) {
            saveUserData()
        }
    }

    private fun startTextEditor() {
        val intent = Intent(this, TextEditorActivity::class.java)
        if(mKnifeText.isNotEmpty()) {
            intent.putExtra("knifeText", mKnifeText)
        }
        startActivityForResult(intent, KNIFE_TEXT)
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
                            CropImages.postNote(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                imgArticleInsertCover.setImageURI(resultUri)
                            }
                            mCoverIsSet = true
                        }
                    }
                    KNIFE_TEXT -> {
                        if(data != null) {
                            mKnifeText = data.getStringExtra("knifeText")
                            txtOpenKinfe.text = KnifeParser.fromHtml(mKnifeText)
                            txtOpenKinfe.alpha = 1F
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {

            }
        }

        return true
    }

    private fun saveUserData() {

        val title = etArticleTitle.text.toString()
        val text = mKnifeText

        if(title.isEmpty() || text.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        if(!mCoverIsSet) {
            makeToast("Você deve escolher uma imagem de capa")
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = sdf.format(Date())

        mPost.title = title
        mPost.text = text
        mPost.embassy_id = mUser.embassy_id

        btArticlePublish.startAnimation()

        val imageName = UUID.randomUUID().toString()
        val fileName = "$imageName.jpg"
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.POST_IMG}/$imageName.jpg")
        mPost.picture_file_name = fileName

        val bitmap = (imgArticleInsertCover.drawable as BitmapDrawable).bitmap
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
                                .add(mPost.toMapNote())
                                .addOnSuccessListener { document ->

                                    document.update("id", document.id).addOnSuccessListener {
                                        btArticlePublish.doneLoadingAnimation(
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
