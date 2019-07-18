package com.balloondigital.egvapp.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val permissions : List<String> = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionConfig.validatePermission(permissions, this)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText("Início")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                textMessage.setText("Search")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_post -> {
                textMessage.setText("Post")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_agenda -> {
                textMessage.setText("Agenda")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_highlights -> {
                textMessage.setText("Destaques")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
