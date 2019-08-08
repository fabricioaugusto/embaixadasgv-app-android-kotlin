package com.balloondigital.egvapp.activity.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.utils.Converters.Companion.drawableToBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCurrentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        mAuth = MyFirebase.auth()

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.tvToLogin2) {
            startLoginActivity()
        }

        if(id == R.id.btSendInvite) {
            sendResetEmail()
        }
    }

    fun setListeners() {
        btSendEmailResetPass.setOnClickListener(this)
        tvToLogin2.setOnClickListener(this)
    }

    fun startLoginActivity() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun sendResetEmail() {

        val email = etSendEmailInvite.text.toString()

        btSendEmailResetPass.startAnimation()

        if(email.isNotEmpty()) {
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                makeToast("Email enviado com sucesso!")
                btSendEmailResetPass.doneLoadingAnimation(
                    resources.getColor(R.color.colorGreen),
                    drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light))
                )
            }.addOnFailureListener {
                makeToast("Este e-mail não está cadastrado!")
                btSendEmailResetPass.revertAnimation()
            }
        }


    }
    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }


}
