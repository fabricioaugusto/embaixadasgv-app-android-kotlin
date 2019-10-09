package com.balloondigital.egvapp.activity.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.RequestCodeActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Invite
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_submit_invite_code.*

class SubmitInviteCodeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mCollections: MyFirebase.COLLECTIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_invite_code)

        mDatabase = MyFirebase.database()
        mCollections = MyFirebase.COLLECTIONS

        setListeners()

    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btSendInviteCode) {
            saveData()
        }

        if(id == R.id.txtRequestCode) {
            startRequestCodeActivity()
        }
    }

    private fun setListeners() {
        btSendInviteCode.setOnClickListener(this)
        txtRequestCode.setOnClickListener(this)
    }

    private fun saveData() {
        val code: String = etInviteCode.text.toString()

        if(code.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        btSendInviteCode.startAnimation()

        mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
            .document(code)
            .get()
            .addOnSuccessListener { document ->

                if(document.exists()) {
                    val invite = document.toObject(Invite::class.java)
                    if(invite != null) {
                        invite.isLeader = document.data?.get("isLeader") as Boolean
                        startRegisterActivity(invite)
                    }  else {
                        makeToast("Código inválido. Insira um código válido!")
                        btSendInviteCode.revertAnimation()
                    }
                }
            }
            .addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
                btSendInviteCode.revertAnimation()
            }
    }

    private fun startRequestCodeActivity() {
        val intent: Intent = Intent(this, RequestCodeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startRegisterActivity(invite: Invite) {
        val intent: Intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("invite", invite)
        startActivity(intent)
        finish()
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
