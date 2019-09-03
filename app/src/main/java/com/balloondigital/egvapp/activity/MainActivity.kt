package com.balloondigital.egvapp.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.balloondigital.egvapp.activity.Create.CreateArticleActivity
import com.balloondigital.egvapp.activity.Create.CreatePostActivity
import com.balloondigital.egvapp.activity.Create.CreateToughtActivity
import com.balloondigital.egvapp.adapter.CreatePostDialogAdapter
import com.balloondigital.egvapp.adapter.MenuListAdapter
import com.balloondigital.egvapp.model.User
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener


class MainActivity : AppCompatActivity() {

    private lateinit var mUsersFragment: Fragment
    private lateinit var mFeedFragment: Fragment
    private lateinit var mHomeFragment: Fragment
    private lateinit var mMenuFragment: Fragment
    private lateinit var mDashboardFragment: Fragment
    private lateinit var mAgendaFragment: Fragment
    private lateinit var mHighlightsFragment: Fragment
    private lateinit var mNavView: BottomNavigationView
    private lateinit var mUser: User
    private lateinit var mAdapter: CreatePostDialogAdapter
    private val permissions : List<String> = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("EGVAPPLIFECYCLE", "onCreate")

        mHomeFragment = HomeFragment()
        mMenuFragment = MenuFragment()
        mFeedFragment = FeedFragment()
        mUsersFragment = UsersFragment()
        mAgendaFragment = AgendaFragment()
        mHighlightsFragment = HighlightsFragment()
        mDashboardFragment = DashboardFragment()

        mAdapter = CreatePostDialogAdapter(this, false, 3)
        mNavView = findViewById(R.id.navView)

        //init vars
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mFeedFragment.arguments = bundle
            mUser = bundle.getSerializable("user") as User
        }

        mDashboardFragment.arguments = bundle
        mMenuFragment.arguments = bundle
        mHomeFragment.arguments = bundle
        mUsersFragment.arguments = bundle
        mAgendaFragment.arguments = bundle
        mHighlightsFragment.arguments = bundle

        PermissionConfig.validatePermission(permissions, this)
        mNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setBottomNavigationView(mNavView)
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setBottomNavigationView(bnv: BottomNavigationView) {

        bnv.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        openFragment(mDashboardFragment)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_home -> {
                openFragment(mDashboardFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                openFragment(mUsersFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_post -> {
                openFragment(mFeedFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_agenda -> {
                openFragment(mAgendaFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_highlights -> {
                openFragment(mMenuFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainViewPager, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun updateUser(user: User) {
        mUser = user
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
