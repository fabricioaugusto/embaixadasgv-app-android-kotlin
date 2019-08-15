package com.balloondigital.egvapp.activity.Create

import android.Manifest
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import android.content.Intent
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.ContextMenu
import android.widget.EditText
import android.view.View.OnLongClickListener
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.internal.NavigationMenu
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import io.github.mthli.knife.KnifeText
import io.github.yavski.fabspeeddial.FabSpeedDial
import kotlinx.android.synthetic.main.activity_create_article.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class CreateArticleActivity : AppCompatActivity(), View.OnClickListener, OnLongClickListener {

    private lateinit var mPost: Post
    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private var mCoverIsSet = false
    private var mFabIsHide = true
    private var mSetTitleIsHide = true
    private var mSetCoverIsHide = true
    private var mPublishPostIsHide = true
    private val GALLERY_CODE: Int = 200
    private val ANIMATE_DURATION: Long = 300
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

        setListeners()
    }

    private fun setListeners() {
        bold.setOnClickListener(this)
        bold.setOnLongClickListener(this)
        italic.setOnClickListener(this)
        italic.setOnLongClickListener(this)
        underline.setOnClickListener(this)
        underline.setOnLongClickListener(this)
        strikethrough.setOnClickListener(this)
        strikethrough.setOnLongClickListener(this)
        bullet.setOnClickListener(this)
        bullet.setOnLongClickListener(this)
        quote.setOnClickListener(this)
        quote.setOnLongClickListener(this)
        link.setOnClickListener(this)
        link.setOnLongClickListener(this)
        clear.setOnClickListener(this)
        clear.setOnLongClickListener(this)
        layoutArticleModal.setOnClickListener(this)
        btArticleSaveTitle.setOnClickListener(this)
        btArticleSaveCover.setOnClickListener(this)
        imgArticleInsertCover.setOnClickListener(this)
        btArticlePublish.setOnClickListener(this)

        val fabListener = object : FabSpeedDial.MenuListener {
            override fun onPrepareMenu(p0: NavigationMenu?): Boolean {
                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.fabArticleTitle -> {
                        showSetTitle()
                        return false
                    }

                    R.id.fabArticlePicture -> {
                        showSetCover()
                        return true
                    }

                    R.id.fabArticlePublish -> {
                        showPublishPost()
                        return false
                    }
                }

                return false
            }

            override fun onMenuClosed() {
            }

        }

        fabSpeedDial.setMenuListener(fabListener)

    }

    override fun onLongClick(view: View): Boolean {
        val id = view.id

        if(id == R.id.bold) {
            return true
        }

        if(id == R.id.italic) {
            return true
        }

        if(id == R.id.underline) {
            return true
        }

        if(id == R.id.strikethrough) {
            return true
        }

        if(id == R.id.bullet) {
            return true
        }

        if(id == R.id.quote) {
            return true
        }

        if(id == R.id.link) {
            return true
        }

        if(id == R.id.clear) {
            return true
        }

        return false
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.bold) {
            knife.bold(!knife.contains(KnifeText.FORMAT_BOLD))
        }

        if(id == R.id.italic) {
            knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC))
        }

        if(id == R.id.underline) {
            knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED))
        }

        if(id == R.id.strikethrough) {
            knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH))
        }

        if(id == R.id.bullet) {
            knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET))
        }

        if(id == R.id.quote) {
            knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE))
        }

        if(id == R.id.link) {
            showLinkDialog()
        }

        if(id == R.id.clear) {
            knife.clearFormats()
        }

        if(id == R.id.layoutArticleModal) {
            if(!mSetTitleIsHide) {
                hideSetTitle()
            }

            if(!mSetCoverIsHide) {
                hideSetCover()
            }

            if(!mPublishPostIsHide) {
                hidePublishPost()
            }
        }

        if(id == R.id.btArticleSaveTitle) {
            hideSetTitle()
        }

        if(id == R.id.btArticleSaveCover) {
            hideSetCover()
        }

        if(id == R.id.imgArticleInsertCover) {
            startGalleryActivity()
        }

        if(id == R.id.btArticlePublish) {
            saveUserData()
        }
    }

    private fun startGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_CODE)
    }

    private fun showLinkDialog() {
        val start = knife.selectionStart
        val end = knife.selectionEnd

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val view = layoutInflater.inflate(R.layout.dialog_link, null, false)
        val editText = view.findViewById(R.id.edit) as EditText
        builder.setView(view)
        builder.setTitle(R.string.dialog_title)

        builder.setPositiveButton(R.string.dialog_button_ok, DialogInterface.OnClickListener { dialog, which ->
            val link = editText.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(link)) {
                return@OnClickListener
            }

            // When KnifeText lose focus, use this method
            knife.link(link, start, end)
        })

        builder.setNegativeButton(R.string.dialog_button_cancel, DialogInterface.OnClickListener { dialog, which ->
            // DO NOTHING HERE
        })

        builder.create().show()
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
                                .setAspectRatio(3, 2)
                                .setFixAspectRatio(true)
                                .start(this)
                        }
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                        if (data != null) {
                            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                            imgArticleInsertCover.setImageURI(result.uri)
                            mCoverIsSet = true
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.knife_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.undo -> knife.undo()
            R.id.redo -> knife.redo()
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {

            }
        }

        return true
    }

    private fun showSetTitle() {
        mSetTitleIsHide = false

        layoutArticleModal.alpha = 0F
        layoutArticleModal.isVisible = true
        layoutArticleSetTitle.isVisible = true
        layoutArticleModal.animate().apply {
            duration = ANIMATE_DURATION
            alpha(1f)
            setListener(null)
            start()
        }
    }

    private fun hideSetTitle() {
        mSetTitleIsHide = true
        layoutArticleModal.animate().alpha(0F).withEndAction {
            layoutArticleSetTitle.isGone = true
            layoutArticleModal.isGone = true
        }.duration = ANIMATE_DURATION
    }

    private fun showSetCover() {
        mSetCoverIsHide = false
        layoutArticleSetPic.isGone = false
        layoutArticleModal.isGone = false
        layoutArticleModal.alpha = 0F
        layoutArticleModal.animate().apply {
            duration = ANIMATE_DURATION
            alpha(1f)
            setListener(null)
            start()
        }
    }

    private fun hideSetCover() {
        mSetCoverIsHide = true
        layoutArticleModal.animate().alpha(0F).withEndAction {
            layoutArticleSetPic.isGone = true
            layoutArticleModal.isGone = true
        }.duration = ANIMATE_DURATION
    }

    private fun showPublishPost() {
        mPublishPostIsHide = false
        layoutArticlePublish.isGone = false
        layoutArticleModal.isGone = false
        layoutArticleModal.animate().apply {
            duration = ANIMATE_DURATION
            alpha(1f)
            setListener(null)
            start()
        }
    }

    private fun hidePublishPost() {
        mSetCoverIsHide = true
        layoutArticleModal.animate().alpha(0F)
        layoutArticlePublish.isGone = true
        layoutArticleModal.isGone = true
    }

    private fun saveUserData() {

        val title = etArticleTitle.text.toString()
        val text = knife.toHtml()

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
        val storagePath: StorageReference = mStorage.child("images/post/article/$imageName.jpg")

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
