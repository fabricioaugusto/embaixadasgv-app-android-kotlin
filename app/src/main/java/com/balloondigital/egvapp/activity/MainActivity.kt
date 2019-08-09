package com.balloondigital.egvapp.activity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.balloondigital.egvapp.activity.Create.CreateArticleActivity
import com.balloondigital.egvapp.activity.Create.CreatePostActivity
import com.balloondigital.egvapp.activity.Create.CreateToughtActivity
import com.balloondigital.egvapp.adapter.CreatePostDialogAdapter
import com.balloondigital.egvapp.model.User
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener


class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUsersFragment: Fragment
    private lateinit var mFeedFragment: Fragment
    private lateinit var mAgendaFragment: Fragment
    private lateinit var mHighlightsFragment: Fragment
    private lateinit var mUser: User
    private lateinit var mAdapter: CreatePostDialogAdapter
    private val permissions : List<String> = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private var mFeedFragmentAdded = false
    private var mUsersFragmentAdded = false
    private var mAgendaFragmentAdded = false
    private var mHighlightsFragmentAdded = false

    override fun onStart() {
        super.onStart()

        mAuth = MyFirebase.auth()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFeedFragment = FeedFragment()
        mUsersFragment = UsersFragment()
        mAgendaFragment = AgendaFragment()
        mHighlightsFragment = HighlightsFragment()

        mAdapter = CreatePostDialogAdapter(this, false, 3)

        //init vars
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mFeedFragment.arguments = bundle
            mUser = bundle.getSerializable("user") as User
        }

        mUsersFragment.arguments = bundle

        PermissionConfig.validatePermission(permissions, this)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setBottomNavigationView(navView)
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {
        makeToast(position.toString())

        if(position == 0) {
            val intent: Intent = Intent(this, CreateToughtActivity::class.java)
            intent.putExtra("user", mUser)
            startActivity(intent)
        }

        if(position == 1) {
            val intent: Intent = Intent(this, CreateArticleActivity::class.java)
            intent.putExtra("user", mUser)
            startActivity(intent)
        }

        if(position == 2) {
            val intent: Intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra("user", mUser)
            startActivity(intent)
        }
    }

    private fun setBottomNavigationView(bnv: BottomNavigationView) {

        bnv.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransition: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransition.add(R.id.mainViewPager, mFeedFragment).commit()
        mFeedFragmentAdded = true
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransition: FragmentTransaction = fragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                if(mFeedFragmentAdded) fragmentTransition.show(mFeedFragment)
                if(mUsersFragmentAdded) fragmentTransition.hide(mUsersFragment)
                if(mAgendaFragmentAdded) fragmentTransition.hide(mAgendaFragment)
                if(mHighlightsFragmentAdded) fragmentTransition.hide(mHighlightsFragment)
                fragmentTransition.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                if(mUsersFragmentAdded) fragmentTransition.show(mUsersFragment)
                else fragmentTransition.add(R.id.mainViewPager, mUsersFragment)
                mUsersFragmentAdded = true

                if(mFeedFragmentAdded) fragmentTransition.hide(mFeedFragment)
                if(mAgendaFragmentAdded) fragmentTransition.hide(mAgendaFragment)
                if(mHighlightsFragmentAdded) fragmentTransition.hide(mHighlightsFragment)
                fragmentTransition.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_post -> {
                setCreatePostDialog()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_agenda -> {
                if(mAgendaFragmentAdded) fragmentTransition.show(mAgendaFragment)
                else fragmentTransition.add(R.id.mainViewPager, mAgendaFragment)
                mAgendaFragmentAdded = true

                if(mFeedFragmentAdded) fragmentTransition.hide(mFeedFragment)
                if(mUsersFragmentAdded) fragmentTransition.hide(mUsersFragment)
                if(mHighlightsFragmentAdded) fragmentTransition.hide(mHighlightsFragment)
                fragmentTransition.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_highlights -> {
                if(mHighlightsFragmentAdded) fragmentTransition.show(mHighlightsFragment)
                else fragmentTransition.add(R.id.mainViewPager, mHighlightsFragment)
                mHighlightsFragmentAdded = true

                if(mFeedFragmentAdded) fragmentTransition.hide(mFeedFragment)
                if(mUsersFragmentAdded) fragmentTransition.hide(mUsersFragment)
                if(mAgendaFragmentAdded) fragmentTransition.hide(mAgendaFragment)
                fragmentTransition.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun setCreatePostDialog() {

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(this)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapter
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setHeader(R.layout.header_dialog)
            dialogBuilder.setPadding(16, 16, 16, 48)
            dialogBuilder.create().show()
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
