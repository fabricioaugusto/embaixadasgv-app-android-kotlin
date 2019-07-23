package com.balloondigital.egvapp.activity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.*
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val permissions : List<String> = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    override fun onStart() {
        super.onStart()

        mAuth = MyFirebase.auth()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val name: String? = currentUser.displayName
            if(name == null) {
                startChooseEmbassyActivity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init vars

        PermissionConfig.validatePermission(permissions, this)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setBottomNavigationView(navView)
    }

    fun startChooseEmbassyActivity() {
        val intent: Intent = Intent(this, ChooseEmbassyActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setBottomNavigationView(bnv: BottomNavigationView) {

        bnv.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransition: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.mainViewPager, FeedFragment()).commit()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransition: FragmentTransaction = fragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentTransition.replace(R.id.mainViewPager, FeedFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                fragmentTransition.replace(R.id.mainViewPager, UsersFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_post -> {
                fragmentTransition.replace(R.id.mainViewPager, CreatePostFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_agenda -> {
                fragmentTransition.replace(R.id.mainViewPager, AgendaFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_highlights -> {
                fragmentTransition.replace(R.id.mainViewPager, HighlightsFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
