package com.balloondigital.egvapp.activity.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.balloondigital.egvapp.R
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.tvToLogin2) {
            startLoginActivity()
        }
    }

    fun setListeners() {
        btResetPassword.setOnClickListener(this)
        tvToLogin2.setOnClickListener(this)
    }

    fun startLoginActivity() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}
