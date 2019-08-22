package com.balloondigital.egvapp.activity.Single

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_photo_single.*

class SinglePhotoActivity : AppCompatActivity() {

    private lateinit var mPhotoUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_single)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mPhotoUrl = bundle.getString("photoUrl").toString()

            Glide.with(this)
                .load(mPhotoUrl)
                .into(pvSingleImage)
        }

        btSinglePhotoClose.setOnClickListener {
            finish()
        }
    }
}
