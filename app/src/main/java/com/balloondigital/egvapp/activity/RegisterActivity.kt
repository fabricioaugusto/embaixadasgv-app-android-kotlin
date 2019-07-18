package com.balloondigital.egvapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.balloondigital.egvapp.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.tvToLogin) {
            startLoginActivity()
        }
    }

    fun setListeners() {
        btRegister.setOnClickListener(this)
        tvToLogin.setOnClickListener(this)
    }

    fun startLoginActivity() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
