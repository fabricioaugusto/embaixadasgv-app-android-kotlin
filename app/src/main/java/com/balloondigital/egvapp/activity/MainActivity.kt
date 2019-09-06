package com.balloondigital.egvapp.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.*
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.balloondigital.egvapp.adapter.CreatePostDialogAdapter
import com.balloondigital.egvapp.fragment.BottomNav.*
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.CustomViewPager



class MainActivity : AppCompatActivity() {

    private lateinit var mViewPager: CustomViewPager
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

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    private fun setBottomNavigationView(bnv: BottomNavigationView) {

        bnv.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        openFragment(mDashboardFragment,0, "dashboard")
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_home -> {
                openFragment(mDashboardFragment, 0, "dashboard")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                openFragment(mUsersFragment, 1, "search")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_post -> {
                openFragment(mFeedFragment, 2, "feed")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_agenda -> {
                openFragment(mAgendaFragment, 3, "agenda")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_highlights -> {
                openFragment(mMenuFragment, 4, "menu")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment, pos: Int, tag: String) {


        val manager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (manager.findFragmentByTag(tag) == null) {
            transaction.add(R.id.mainViewPager, fragment, tag)
        }

        val dashboardFragment: Fragment? = manager.findFragmentByTag("dashboard")
        val searchFragment: Fragment? = manager.findFragmentByTag("search")
        val feedFragment: Fragment? = manager.findFragmentByTag("feed")
        val agendaFragment: Fragment? = manager.findFragmentByTag("agenda")
        val menuFragment: Fragment? = manager.findFragmentByTag("menu")

        if(dashboardFragment != null) {
            transaction.hide(dashboardFragment)
        }

        if(searchFragment != null) {
            transaction.hide(searchFragment)
        }

        if(feedFragment != null) {
            transaction.hide(feedFragment)
        }

        if(agendaFragment != null) {
            transaction.hide(agendaFragment)
        }

        if(menuFragment != null) {
            transaction.hide(menuFragment)
        }

        transaction.show(fragment)

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
