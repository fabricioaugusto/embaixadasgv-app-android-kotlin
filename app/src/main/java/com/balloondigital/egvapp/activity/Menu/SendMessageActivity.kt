package com.balloondigital.egvapp.activity.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.AppMessage
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters.Companion.drawableToBitmap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_send_message.*

class SendMessageActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        supportActionBar!!.title = "Envie-nos uma mensagem"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

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

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btSendAppMessage) {
            sendMessage()
        }
    }

    private fun setListeners() {
        btSendAppMessage.setOnClickListener(this)
    }

    private fun sendMessage() {

        val message = etSendMessage.text.toString()

        if(message.isEmpty()) {
            return
        }

        btSendAppMessage.startAnimation()

        val appMessage = AppMessage(user_id = mUser.id,
            user_city = mUser.city!!,
            user_embassy = mUser.embassy.name,
            type = "message",
            message = message,
            user = mUser)

        mDatabase.collection(MyFirebase.COLLECTIONS.APP_MESSAGES)
            .add(appMessage.toMap())
            .addOnSuccessListener {
                documentReference ->

                documentReference.update("id", documentReference.id)
                etSendMessage.setText("")

                btSendAppMessage.doneLoadingAnimation(
                    resources.getColor(R.color.colorGreen),
                    drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                )
            }
    }
}
