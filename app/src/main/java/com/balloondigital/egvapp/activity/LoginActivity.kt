package com.balloondigital.egvapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.balloondigital.egvapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btLogin) {
            startMainActivity()
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
}
