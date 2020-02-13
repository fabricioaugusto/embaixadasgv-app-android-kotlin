package com.balloondigital.egvapp.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.CreatePostDialogAdapter
import com.balloondigital.egvapp.fragment.BottomNav.*
import com.balloondigital.egvapp.fragment.HighlightsFragment
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.CustomViewPager
import com.balloondigital.egvapp.utils.PermissionConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode

class MainInfluencersActivity : AppCompatActivity() {


    private lateinit var mViewPager: CustomViewPager
    private lateinit var mUsersFragment: Fragment
    private lateinit var mFeedFragment: Fragment
    private lateinit var mMenuFragment: Fragment
    private lateinit var mAgendaFragment: Fragment
    private lateinit var mHighlightsFragment: Fragment
    private lateinit var mNavView: BottomNavigationView
    private var mTabSelected: Int = 0
    private lateinit var mUser: User
    private lateinit var mAdapter: CreatePostDialogAdapter
    private val permissions : List<String> = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_influencers)

        mMenuFragment = MenuFragment()
        mFeedFragment = FeedFragment()
        mUsersFragment = UsersFragment()
        mAgendaFragment = AgendaFragment()
        mHighlightsFragment = HighlightsFragment()

        mAdapter = CreatePostDialogAdapter(this, false, 3)
        mNavView = findViewById(R.id.influencersNavView)

        //init vars
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mFeedFragment.arguments = bundle
            mUser = bundle.getSerializable("user") as User
        }

        mMenuFragment.arguments = bundle
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
        val usersEnrollments: Fragment? = manager.findFragmentByTag("${R.id.agendaViewPager}:enrollmentUsers")
        val usersLikes: Fragment? = manager.findFragmentByTag("${R.id.feedViewPager}:likeUsers")
        val singleUser: Fragment? = manager.findFragmentByTag("singleUser")
        val singleLikeUser: Fragment? = manager.findFragmentByTag("${R.id.feedViewPager}:singleUser")
        val singleEnrollmentUser: Fragment? = manager.findFragmentByTag("${R.id.agendaViewPager}:singleUser")


        if(mTabSelected == 0) {

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

        if(mTabSelected == 1) {
            if (singleUser != null && singleUser.isVisible) {
                transaction.remove(singleUser).commit()
                return
            }
        }

        if(mTabSelected == 2) {
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

        if(mTabSelected == 3) {

            val menuViewPager = R.id.menuViewPager

            val singleMenuUser: Fragment? = manager.findFragmentByTag("${menuViewPager}:singleUser")
            val usersEventEnrollments: Fragment? = manager.findFragmentByTag("${menuViewPager}:enrollmentUsers")
            val singleMenuEvent: Fragment? = manager.findFragmentByTag("${menuViewPager}:singleEvent")
            val singleEmbassy: Fragment? = manager.findFragmentByTag("${menuViewPager}:singleEmbassy")
            val singleBulletin: Fragment? = manager.findFragmentByTag("${menuViewPager}:singleBulletin")
            val embassyAgendaMenu: Fragment? = manager.findFragmentByTag("${menuViewPager}:embassyAgenda")
            val embassyPhotosMenu: Fragment? = manager.findFragmentByTag("${menuViewPager}:embassyPhotos")
            val embassyMembersMenu: Fragment? = manager.findFragmentByTag("${menuViewPager}:embassyMembers")
            val listEmbassy: Fragment? = manager.findFragmentByTag("${menuViewPager}:listEmbassy")
            val manageEvents: Fragment? = manager.findFragmentByTag("${menuViewPager}:manageEvents")
            val manageBulletins: Fragment? = manager.findFragmentByTag("${menuViewPager}:manageBulletins")
            val report: Fragment? = manager.findFragmentByTag("${menuViewPager}:report")
            val manageSponsors: Fragment? = manager.findFragmentByTag("${menuViewPager}:manageSponsors")
            val affiliatedEmbassies: Fragment? = manager.findFragmentByTag("${menuViewPager}:affiliatedEmbassies")
            val managePhotos: Fragment? = manager.findFragmentByTag("${menuViewPager}:managePhotos")
            val approvalInvitationRequests: Fragment? = manager.findFragmentByTag("${menuViewPager}:approvalInvitationRequests")
            val interestedList: Fragment? = manager.findFragmentByTag("${menuViewPager}:interestedList")

            if (singleMenuUser != null && singleMenuUser.isVisible) {
                transaction.remove(singleMenuUser).commit()
                return
            }

            if (singleBulletin != null && singleBulletin.isVisible) {
                transaction.remove(singleBulletin).commit()
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

            if (embassyPhotosMenu != null && embassyPhotosMenu.isVisible) {
                transaction.remove(embassyPhotosMenu).commit()
                return
            }

            if (embassyAgendaMenu != null && embassyAgendaMenu.isVisible) {
                transaction.remove(embassyAgendaMenu).commit()
                return
            }

            if (embassyMembersMenu != null && embassyMembersMenu.isVisible) {
                transaction.remove(embassyMembersMenu).commit()
                return
            }

            if (singleEmbassy != null && singleEmbassy.isVisible) {
                transaction.remove(singleEmbassy).commit()
                return
            }

            if (managePhotos != null && managePhotos.isVisible) {
                transaction.remove(managePhotos).commit()
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

            if (report != null && report.isVisible) {
                transaction.remove(report).commit()
                return
            }

            if (interestedList != null && interestedList.isVisible) {
                transaction.remove(interestedList).commit()
                return
            }

            if (approvalInvitationRequests != null && approvalInvitationRequests.isVisible) {
                transaction.remove(approvalInvitationRequests).commit()
                return
            }

            if (manageSponsors != null && manageSponsors.isVisible) {
                transaction.remove(manageSponsors).commit()
                return
            }

            if (affiliatedEmbassies != null && affiliatedEmbassies.isVisible) {
                transaction.remove(affiliatedEmbassies).commit()
                return
            }

            if (listEmbassy != null && listEmbassy.isVisible) {
                transaction.remove(listEmbassy).commit()
                return
            }
        }


        if(mNavView.selectedItemId != R.id.navigation_post) {
            mNavView.selectedItemId = R.id.navigation_post
        } else {
            finish()
        }

    }

    private fun setBottomNavigationView(bnv: BottomNavigationView) {

        bnv.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        val manager = supportFragmentManager
        val feedFragment: Fragment? = manager.findFragmentByTag("feed")

        if(feedFragment == null) {
            openFragment(mFeedFragment,0, "feed")
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_post -> {
                openFragment(mFeedFragment, 0, "feed")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                openFragment(mUsersFragment, 1, "search")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_agenda -> {
                openFragment(mAgendaFragment, 2, "agenda")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_highlights -> {
                openFragment(mMenuFragment, 3, "menu")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment, pos: Int, tag: String) {

        mTabSelected = pos

        val manager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

        val feedFragment: Fragment? = manager.findFragmentByTag("feed")
        val searchFragment: Fragment? = manager.findFragmentByTag("search")
        val agendaFragment: Fragment? = manager.findFragmentByTag("agenda")
        val menuFragment: Fragment? = manager.findFragmentByTag("menu")

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

        if (currentFragment == null) {
            transaction.add(R.id.influencersMainViewPager, fragment, tag)
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
