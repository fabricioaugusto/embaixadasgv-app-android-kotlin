package com.balloondigital.egvapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = MyFirebase.auth()

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btLogin) {
            loginUser()
        }

        if(id == R.id.tvToRegister) {
            startRegisterActivity()
        }

        if(id == R.id.tvForgotPassword) {
            startResetPasswordActivity()
        }

    }

    fun setListeners() {
        btLogin.setOnClickListener(this)
        tvToRegister.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
    }

    fun startMainActivity() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun startRegisterActivity() {
        val intent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun startResetPasswordActivity() {
        val intent: Intent = Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    fun loginUser() {

        val email = txtLoginEmail.text.toString()
        val pass = txtLoginPass.text.toString()

        if(email.isEmpty() || pass.isEmpty()) {
            makeToast("Você deve preencher os dois campos!")
            return
        }

        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    makeToast("Login efetuado com sucesso!")
                    val user = mAuth.currentUser
                    startMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    makeToast("Dados de login incorretos! Tente novamente!")

                }

                // ...
            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
