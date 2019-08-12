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


class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var mUsersFragment: Fragment
    private lateinit var mFeedFragment: Fragment
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mAgendaFragment: Fragment
    private lateinit var mHighlightsFragment: Fragment
    private lateinit var mNavView: BottomNavigationView
    private lateinit var mUser: User
    private lateinit var mAdapter: CreatePostDialogAdapter
    private val permissions : List<String> = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private var mFeedFragmentAdded = false
    private var mUsersFragmentAdded = false
    private var mAgendaFragmentAdded = false
    private var mHighlightsFragmentAdded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("EGVAPPLIFECYCLE", "onCreate")

        mFeedFragment = FeedFragment()
        mUsersFragment = UsersFragment()
        mAgendaFragment = AgendaFragment()
        mHighlightsFragment = HighlightsFragment()

        mAdapter = CreatePostDialogAdapter(this, false, 3)
        mNavView = findViewById(R.id.navView)

        //init vars
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mFeedFragment.arguments = bundle
            mUser = bundle.getSerializable("user") as User
        }

        mUsersFragment.arguments = bundle
        mAgendaFragment.arguments = bundle

        PermissionConfig.validatePermission(permissions, this)
        mNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setBottomNavigationView(mNavView)
    }

    override fun onResume() {
        super.onResume()
        Log.d("EGVAPPLIFECYCLE", "onResume")
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {
        makeToast(position.toString())

        mCPDialog.dismiss()

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

        if(mFeedFragmentAdded) fragmentTransition.show(mFeedFragment)
        else fragmentTransition.add(R.id.mainViewPager, mFeedFragment).commit()

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
                return@OnNavigationItemSelectedListener false
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

    private fun setCreatePostDialog() {

        mNavView.selectedItemId = R.id.home
        mNavView.menu.getItem(0).isChecked = true

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(this)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapter
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setHeader(R.layout.header_dialog)
            dialogBuilder.setPadding(16, 16, 16, 48)
            mCPDialog = dialogBuilder.create()
            mCPDialog.show()
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
