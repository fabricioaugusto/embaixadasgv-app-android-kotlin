package com.balloondigital.egvapp.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.BasicUser
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import io.github.mthli.knife.KnifeText
import kotlinx.android.synthetic.main.activity_create_article.*
import kotlinx.android.synthetic.main.activity_create_tought.*
import kotlinx.android.synthetic.main.activity_create_tought.bold
import kotlinx.android.synthetic.main.activity_create_tought.clear
import kotlinx.android.synthetic.main.activity_create_tought.italic
import kotlinx.android.synthetic.main.activity_create_tought.knife
import kotlinx.android.synthetic.main.activity_create_tought.link
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CreateToughtActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mPost: Post
    private lateinit var mUser: BasicUser
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private var mPublishToughtIsHide = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tought)

        supportActionBar!!.title = "Nova Nota"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as BasicUser
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        mPost = Post()
        mPost.type = "thought"
        mPost.user = mUser

        setListeners()
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.bold) {
            knife.bold(!knife.contains(KnifeText.FORMAT_BOLD))
        }

        if(id == R.id.italic) {
            knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC))
        }

        if(id == R.id.link) {
            showLinkDialog()
        }

        if(id == R.id.clear) {
            knife.clearFormats()
        }

        if(id == R.id.fabCreateTought) {
            showPublishTought()
        }

        if(id == R.id.layoutToughtModal) {
            if(!mPublishToughtIsHide) {
                hidePublishTought()
            }
        }

        if(id == R.id.btToughtPublish) {
            saveUserData()
        }
    }

    private fun setListeners() {
        bold.setOnClickListener(this)
        italic.setOnClickListener(this)
        link.setOnClickListener(this)
        clear.setOnClickListener(this)
        fabCreateTought.setOnClickListener(this)
        layoutToughtModal.setOnClickListener(this)
        btToughtPublish.setOnClickListener(this)

    }

    private fun showPublishTought() {
        mPublishToughtIsHide = false
        layoutToughtPublish.isGone = false
        layoutToughtModal.isGone = false
        layoutToughtModal.animate().alpha(1.0F)
    }

    private fun hidePublishTought() {
        mPublishToughtIsHide = true
        layoutToughtModal.animate().alpha(0F)
        layoutToughtPublish.isGone = true
        layoutToughtModal.isGone = true
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

    private fun saveUserData() {
        val text = knife.toHtml()

        if(text.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = sdf.format(Date())

        mPost.thought = text
        mPost.date = Timestamp(System.currentTimeMillis())

        btToughtPublish.startAnimation()

        mDatabase.collection(mCollections.POSTS)
            .add(mPost.toMapTought())
            .addOnSuccessListener { document ->

                document.update("id", document.id).addOnSuccessListener {
                    btToughtPublish.doneLoadingAnimation(
                        resources.getColor(R.color.colorGreen),
                        Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                    ).apply {
                        finish()
                    }
                }
            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }


}
