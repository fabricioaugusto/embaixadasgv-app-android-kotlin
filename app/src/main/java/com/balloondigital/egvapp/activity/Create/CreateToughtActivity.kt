package com.balloondigital.egvapp.activity.Create

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Edit.TextEditorActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_create_tought.*
import kotlinx.android.synthetic.main.activity_create_tought.txtOpenKinfe
import java.lang.Exception

class CreateToughtActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mPost: Post
    private lateinit var mUser: User
    private lateinit var mKnifeText: String
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private val KNIFE_TEXT = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tought)

        supportActionBar!!.title = "Nova Publicação"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        mPost = Post()
        mPost.type = "thought"
        mPost.user_id = mUser.id
        mPost.user = mUser

        mKnifeText = ""

        setListeners()
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
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> true
        }
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.txtOpenKinfe) {
            startTextEditor()
        }

        if(id == R.id.btToughtPublish) {
            saveUserData()
        }
    }

    private fun setListeners() {
        txtOpenKinfe.setOnClickListener(this)
        btToughtPublish.setOnClickListener(this)
    }

    private fun startTextEditor() {
        val intent = Intent(this, TextEditorActivity::class.java)
        if(mKnifeText.isNotEmpty()) {
            intent.putExtra("knifeText", mKnifeText)
        }
        startActivityForResult(intent, KNIFE_TEXT)
    }

    private fun saveUserData() {
        val text = mKnifeText

        if(text.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        mPost.text = text
        mPost.embassy_id = mUser.embassy_id

        btToughtPublish.startAnimation()

        mDatabase.collection(mCollections.POSTS)
            .add(mPost.toMapTought())
            .addOnSuccessListener { document ->

                document.update("id", document.id).addOnSuccessListener {
                    btToughtPublish.doneLoadingAnimation(
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
