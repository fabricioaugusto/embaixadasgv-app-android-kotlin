package com.balloondigital.egvapp.activity

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
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


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("EGVAPPLIFECYCLE", "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("EGVAPPLIFECYCLE", "onDetachedFromWindow")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("EGVAPPLIFECYCLE", "onDestroy")
    }

    override fun onPause() {
        super.onPause()
        Log.d("EGVAPPLIFECYCLE", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("EGVAPPLIFECYCLE", "onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.d("EGVAPPLIFECYCLE", "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("EGVAPPLIFECYCLE", "onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("EGVAPPLIFECYCLE", "onStart")
    }

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
        val singleEvent: Fragment? = manager.findFragmentByTag("${R.id.agendaViewPager}:singleEvent")
        val singleEmbassyEvent: Fragment? = manager.findFragmentByTag("${R.id.dashboardViewPager}:singleEvent")
        val embassyMembers: Fragment? = manager.findFragmentByTag("embassyMembers")
        val embassyAgenda: Fragment? = manager.findFragmentByTag("embassyAgenda")
        val embassyPhotos: Fragment? = manager.findFragmentByTag("embassyPhotos")
        val usersEnrollments: Fragment? = manager.findFragmentByTag("${R.id.agendaViewPager}:enrollmentUsers")
        val usersEmbassyEnrollments: Fragment? = manager.findFragmentByTag("${R.id.dashboardViewPager}:enrollmentUsers")
        val usersLikes: Fragment? = manager.findFragmentByTag("${R.id.feedViewPager}:likeUsers")
        val singleUser: Fragment? = manager.findFragmentByTag("singleUser")
        val singleEmbassyUser: Fragment? = manager.findFragmentByTag("singleEmbassyUser")
        val singleLikeUser: Fragment? = manager.findFragmentByTag("${R.id.feedViewPager}:singleUser")
        val singleEnrollmentUser: Fragment? = manager.findFragmentByTag("${R.id.agendaViewPager}:singleUser")
        val singleMenuUser: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:singleUser")

        if(mTabSelected == 0) {

            val singleBulletin: Fragment? = manager.findFragmentByTag("${R.id.dashboardViewPager}:singleBulletin")

            if (singleEmbassyUser != null && singleEmbassyUser.isVisible) {
                transaction.remove(singleEmbassyUser).commit()
                return
            }

            if (singleBulletin != null && singleBulletin.isVisible) {
                transaction.remove(singleBulletin).commit()
                return
            }

            if (usersEmbassyEnrollments != null && usersEmbassyEnrollments.isVisible) {
                transaction.remove(usersEmbassyEnrollments).commit()
                return
            }

            if (singleEmbassyEvent != null && singleEmbassyEvent.isVisible) {
                transaction.remove(singleEmbassyEvent).commit()
                return
            }

            if (embassyMembers != null && embassyMembers.isVisible) {
                transaction.remove(embassyMembers).commit()
                return
            }

            if (embassyAgenda != null && embassyAgenda.isVisible) {
                transaction.remove(embassyAgenda).commit()
                return
            }
            if (embassyPhotos != null && embassyPhotos.isVisible) {
                transaction.remove(embassyPhotos).commit()
                return
            }
        }

        if(mTabSelected == 1) {
            if (singleUser != null && singleUser.isVisible) {
                transaction.remove(singleUser).commit()
                return
            }
        }

        if(mTabSelected == 2) {

            if (singleLikeUser != null && singleLikeUser.isVisible) {
                transaction.remove(singleLikeUser).commit()
                return
            }

            if (singleUserPost != null && singleUserPost.isVisible) {
                transaction.remove(singleUserPost).commit()
                return
            }

            if (usersLikes != null && usersLikes.isVisible) {
                transaction.remove(usersLikes).commit()
                return
            }

            if (singlePost != null && singlePost.isVisible) {
                transaction.remove(singlePost).commit()
                return
            }

        }

        if(mTabSelected == 3) {
            if (singleEnrollmentUser != null && singleEnrollmentUser.isVisible) {
                transaction.remove(singleEnrollmentUser).commit()
                return
            }

            if (usersEnrollments != null && usersEnrollments.isVisible) {
                transaction.remove(usersEnrollments).commit()
                return
            }

            if (singleEvent != null && singleEvent.isVisible) {
                transaction.remove(singleEvent).commit()
                return
            }
        }

        if(mTabSelected == 4) {

            val usersEventEnrollments: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:enrollmentUsers")
            val singleMenuEvent: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:singleEvent")
            val singleEmbassy: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:singleEmbassy")
            val singleBulletin: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:singleBulletin")
            val listEmbassy: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:listEmbassy")
            val manageEvents: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:manageEvents")
            val manageBulletins: Fragment? = manager.findFragmentByTag("${R.id.menuViewPager}:manageBulletins")

            if (singleMenuUser != null && singleMenuUser.isVisible) {
                transaction.remove(singleMenuUser).commit()
                return
            }

            if (singleBulletin != null && singleBulletin.isVisible) {
                transaction.remove(singleBulletin).commit()
                return
            }

            if (singleEmbassy != null && singleEmbassy.isVisible) {
                transaction.remove(singleEmbassy).commit()
                return
            }

            if (usersEventEnrollments != null && usersEventEnrollments.isVisible) {
                transaction.remove(usersEventEnrollments).commit()
                return
            }

            if (singleMenuEvent != null && singleMenuEvent.isVisible) {
                transaction.remove(singleMenuEvent).commit()
                return
            }

            if (manageEvents != null && manageEvents.isVisible) {
                transaction.remove(manageEvents).commit()
                return
            }

            if (manageBulletins != null && manageBulletins.isVisible) {
                transaction.remove(manageBulletins).commit()
                return
            }

            if (listEmbassy != null && listEmbassy.isVisible) {
                transaction.remove(listEmbassy).commit()
                return
            }
        }
    }

    private fun setBottomNavigationView(bnv: BottomNavigationView) {

        bnv.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        val manager = supportFragmentManager
        val dashboardFragment: Fragment? = manager.findFragmentByTag("dashboard")

        if(dashboardFragment == null) {
            openFragment(mDashboardFragment,0, "dashboard")
        }
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

        val currentFragment = manager.findFragmentByTag(tag)
        Log.d("EGVAPPMANAGEFRAGMENT", "$tag ${currentFragment.toString()}")

        if (currentFragment == null) {
            Log.d("EGVAPPMANAGEFRAGMENT", "adicionou")
            transaction.add(R.id.mainViewPager, fragment, tag)
        } else {
            transaction.show(currentFragment)
        }

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
