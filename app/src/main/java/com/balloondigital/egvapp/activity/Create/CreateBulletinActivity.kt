package com.balloondigital.egvapp.activity.Create

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Edit.TextEditorActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Bulletin
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_create_bulletin.*
import java.lang.Exception

class CreateBulletinActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBulletin: Bulletin
    private lateinit var mKnifeText: String
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private val GALLERY_CODE: Int = 200
    private val KNIFE_TEXT = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_bulletin)

        supportActionBar!!.title = "Novo Informativo"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        mBulletin = Bulletin()

        mKnifeText = ""

        setListeners()
    }

    private fun setListeners() {
        btBulletinPublish.setOnClickListener(this)
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

        if(id == R.id.btBulletinPublish) {
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

        val title = etBulletinTitle.text.toString()
        val resume = etBulletinResume.text.toString()
        val text = mKnifeText

        if(title.isEmpty() || text.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        mBulletin.title = title
        mBulletin.text = text
        mBulletin.resume = resume

        btBulletinPublish.startAnimation()

        mDatabase.collection(mCollections.BULLETIN)
            .add(mBulletin.toMap())
            .addOnSuccessListener { document ->

                document.update("id", document.id).addOnSuccessListener {
                    btBulletinPublish.doneLoadingAnimation(
                        resources.getColor(R.color.colorGreen),
                        Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                    ).apply {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                }
            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
