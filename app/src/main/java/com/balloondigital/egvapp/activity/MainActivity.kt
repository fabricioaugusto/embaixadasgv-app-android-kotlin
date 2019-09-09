package com.balloondigital.egvapp.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
    private var mTabSelected: Int = 0
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

        val manager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        val singlePost: Fragment? = manager.findFragmentByTag("singlePost")
        val singleUserPost: Fragment? = manager.findFragmentByTag("singlePostUser")
        val singleEvent: Fragment? = manager.findFragmentByTag("singleEvent")
        val singleEmbassyEvent: Fragment? = manager.findFragmentByTag("singleEmbassyEvent")
        val embassyMembers: Fragment? = manager.findFragmentByTag("embassyMembers")
        val embassyAgenda: Fragment? = manager.findFragmentByTag("embassyAgenda")
        val embassyPhotos: Fragment? = manager.findFragmentByTag("embassyPhotos")
        val singleUser: Fragment? = manager.findFragmentByTag("singleUser")
        val singleEmbassyUser: Fragment? = manager.findFragmentByTag("singleEmbassyUser")

        if(mTabSelected == 0) {

            if (singleEmbassyEvent != null && singleEmbassyEvent.isVisible) {
                makeToast("embassyPhotos is Visible")
                transaction.remove(singleEmbassyEvent).commit()
                return
            }

            if (singleEmbassyUser != null && singleEmbassyUser.isVisible) {
                makeToast("embassyPhotos is Visible")
                transaction.remove(singleEmbassyUser).commit()
                return
            }


            if (embassyMembers != null && embassyMembers.isVisible) {
                makeToast("embassyMembers is Visible")
                transaction.remove(embassyMembers).commit()
                return
            }

            if (embassyAgenda != null && embassyAgenda.isVisible) {
                makeToast("embassyAgenda is Visible")
                transaction.remove(embassyAgenda).commit()
                return
            }
            if (embassyPhotos != null && embassyPhotos.isVisible) {
                makeToast("embassyPhotos is Visible")
                transaction.remove(embassyPhotos).commit()
                return
            }
        }

        if(mTabSelected == 1) {
            if (singleUser != null && singleUser.isVisible) {
                makeToast("singleUser is Visible")
                transaction.remove(singleUser).commit()
                return
            }
        }

        if(mTabSelected == 2) {
            if (singlePost != null && singlePost.isVisible) {
                makeToast("singlePost is Visible")
                transaction.remove(singlePost).commit()
                return
            }
            if (singleUserPost != null && singleUserPost.isVisible) {
                makeToast("singleUserPost is Visible")
                transaction.remove(singleUserPost).commit()
                return
            }
        }

        if(mTabSelected == 3) {
            if (singleEvent != null && singleEvent.isVisible) {
                makeToast("singleEvent is Visible")
                transaction.remove(singleEvent).commit()
                return
            }
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

        mTabSelected = pos

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
