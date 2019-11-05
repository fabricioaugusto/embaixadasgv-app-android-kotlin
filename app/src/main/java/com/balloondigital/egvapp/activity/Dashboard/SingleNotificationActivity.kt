package com.balloondigital.egvapp.activity.Dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Notification
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_single_notification.*

class SingleNotificationActivity : AppCompatActivity(){

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mNotification: Notification
    private lateinit var mNotificationID: String
    private lateinit var mNotificationTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_notification)

        mNotificationTitle = ""

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mNotificationID = bundle.getString("notificationID", "")
            mNotificationTitle = bundle.getString("notificationTitle", "")
        }

        supportActionBar!!.title = "Notificação"
        if(mNotificationTitle.isNotEmpty()) {
            supportActionBar!!.title = mNotificationTitle
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()

        getSingleBulletin()
        setListeners()
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

    override fun onBackPressed() {
        finish()
    }

    private fun setListeners() {

    }

    private fun getSingleBulletin() {
        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .document(mNotificationID)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val notification = documentSnapshot.toObject(Notification::class.java)
                if(notification != null) {
                    mNotification = notification
                    bindData()
                }
            }
    }

    private fun bindData() {

        val postDate = Converters.dateToString(mNotification.created_at!!)
        txtNotificationDate.text = "${postDate.date}/${postDate.month}/${postDate.fullyear}"

        if(mNotification.picture.isNotEmpty()) {
            Glide.with(this)
                .load(mNotification.picture.toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgNotificationPicture)
            imgNotificationPicture.isGone = false
        } else {
            Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/egv-app-f851e.appspot.com/o/assets%2Fimages%2Fbg_egv_logo.png?alt=media&token=90971d90-b517-47c5-a3c8-cede129cba3e")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgNotificationPicture)
            imgNotificationPicture.isGone = false
        }

        txtNotificationTitle.text = mNotification.title
        txtNotificationDescription.text = mNotification.description
        txtNotificationText.text = KnifeParser.fromHtml(mNotification.text)

    }
}
