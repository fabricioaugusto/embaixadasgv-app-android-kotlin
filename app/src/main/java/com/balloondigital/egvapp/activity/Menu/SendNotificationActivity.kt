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
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Edit.TextEditorActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Notification
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
import kotlinx.android.synthetic.main.activity_send_notification.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class SendNotificationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mNotification: Notification
    private lateinit var mUser: User
    private lateinit var mKnifeText: String
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mStorage: StorageReference
    private lateinit var mCollections: MyFirebase.COLLECTIONS
    private var mCoverIsSet = false
    private var mTextCheck = true
    private val GALLERY_CODE: Int = 200
    private val KNIFE_TEXT = 500
    private val permissions: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_notification)
        supportActionBar!!.title = "Nova Publicação"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        PermissionConfig.validatePermission(permissions, this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mNotification = Notification()
        mDatabase = MyFirebase.database()
        mStorage = MyFirebase.storage()
        mCollections = MyFirebase.COLLECTIONS

        mKnifeText = ""

        setListeners()
    }

    private fun setListeners() {
        imgNotificationInsert.setOnClickListener(this)
        btNotificationPublish.setOnClickListener(this)
        txtOpenKinfe.setOnClickListener(this)
    }

    override fun onClick(view: View) {

        val id = view.id


        if(id == R.id.imgNotificationInsert) {
            startGalleryActivity()
        }

        if(id == R.id.txtOpenKinfe) {
            startTextEditor()
        }

        if(id == R.id.btNotificationPublish) {
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
                            CropImages.profilePicture(this, uri)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null) {
                            val resultUri: Uri? = UCrop.getOutput(data)
                            if(resultUri != null) {
                                imgNotificationInsert.setImageDrawable(null)
                                imgNotificationInsert.setImageURI(resultUri)
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

        val title = etNotificationTitle.text.toString()
        val description = etNotificationDescription.text.toString()
        val text = mKnifeText

        if(title.isEmpty() || text.isEmpty() || description.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        if(!mCoverIsSet) {
            makeToast("Você deve escolher uma imagem de capa")
            return
        }

        mNotification.title = title
        mNotification.description = description
        mNotification.text = text
        mNotification.type = "manager_notification"

        btNotificationPublish.startAnimation()

        while (mTextCheck) {

            val lastStr = mNotification.text.takeLast(4)

            if(lastStr == "<br>") {
                mNotification.text = mNotification.text.dropLast(4)
            } else {
                mTextCheck = false
            }
        }

        val imageName = UUID.randomUUID().toString()
        val storagePath: StorageReference = mStorage.child("${MyFirebase.STORAGE.POST_IMG}/$imageName.jpg")

        val bitmap = (imgNotificationInsert.drawable as BitmapDrawable).bitmap
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
                            mNotification.picture = uri.toString()
                            mDatabase.collection(mCollections.NOTIFICATIONS)
                                .add(mNotification.toMap())
                                .addOnSuccessListener { document ->
                                    mNotification.id = document.id
                                    document.update("id", document.id).addOnSuccessListener {
                                        buildNotification()
                                    }
                                }
                        }
                    })
                }
            }

            Toast.makeText(this, "Imagem carregada com sucesso!", Toast.LENGTH_LONG).show()
        }
    }

    private fun buildNotification() {


        val notificacao = JSONObject()
        val dados = JSONObject()

        try {

            var topic = ""

            if(radioBtSendToLeaders.isChecked) {
                notificacao.put("to", "/topics/egv_topic_leaders")
            }

            if(radioBtSendToAll.isChecked) {
                notificacao.put("to", "/topics/egv_topic_members")
            }

            dados.put("description", mNotification.description)
            dados.put("title", mNotification.title)
            dados.put("picture", mNotification.picture)
            dados.put("id", mNotification.id)


            notificacao.put("data", dados)

            val site = "https://fcm.googleapis.com/fcm/send"

            sendNotification(site, notificacao)


        } catch (e: Exception) {

        }

    }

    private fun sendNotification(site: String, notification: JSONObject) {


        val requestQueue = Volley.newRequestQueue(this)


        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, site, notification,
            Response.Listener { },
            Response.ErrorListener { }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {


                val header = HashMap<String, String>()

                header["Authorization"] =
                    "key=AAAABeFcUSY:APA91bFUIfjYCbM3HldgyZNxz4y9T72K5thDiloTsnaABpPXoFjs5qeBbMELC76YKQLfSfr8L01KVdWjvkVd9O_Wpzsq7_YX7cAnqlbO62xb5WIICDUC7ix_T3HojxN7-VZ4jN22wScP"


                header["Content-Type"] = "application/json"

                return header

            }
        }
        requestQueue.add(jsonObjectRequest)

        btNotificationPublish.doneLoadingAnimation(
            resources.getColor(R.color.colorGreen),
            Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
        )


    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}