package com.balloondigital.egvapp.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Auth.CheckAuthActivity
import com.balloondigital.egvapp.activity.Create.CreateEventActivity
import com.balloondigital.egvapp.activity.Edit.ChangePassActivity
import com.balloondigital.egvapp.activity.Edit.ChangeProfilePhotoActivity
import com.balloondigital.egvapp.activity.Edit.EditProfileActivity
import com.balloondigital.egvapp.activity.Edit.EditSocialActivity
import com.balloondigital.egvapp.activity.Menu.*
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.adapter.MenuListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.MenuItens
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_menu.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuFragment : Fragment() {

    private lateinit var mUser: User
    private lateinit var mContext: Context
    private lateinit var mMenuList: ListView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: MenuListAdapter
    private lateinit var mMenuItensList: List<String>
    private lateinit var mMenuSectionList: List<String>
    private val MENU_REQUEST_CODE: Int = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_menu, container, false)

        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()
        mContext = view.context
        mMenuList = view.findViewById(R.id.listViewMenu)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }
        mAdapter = MenuListAdapter(mContext, mUser)

        setListView()

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.d("EGVAPPLOGFRAGMENT", "onStart")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MENU_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if(data != null) {
                    mUser = data.getSerializableExtra("user") as User
                    mAdapter = MenuListAdapter(mContext, mUser)
                    setListView()
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private fun setListView() {

        mMenuItensList = MenuItens.menuList
        mMenuSectionList = MenuItens.menuSectionList

        var sectionIndex = 0

        for(item in mMenuItensList) {
            if(item == "section") {
                mAdapter.addSectionHeaderItem(mMenuSectionList[sectionIndex])
                sectionIndex += 1
            } else {
                mAdapter.addItem(item)
            }
        }

        mMenuList.adapter = mAdapter

        val listViewListener = AdapterView.OnItemClickListener { adapter, view, pos, posLong ->

            when(mMenuItensList[pos]) {
                MenuItens.profile -> startUserProfileActivity()
                MenuItens.editProfile -> startEditProfileActivity()
                MenuItens.changeProfilePhoto -> startChangeProfilePhotoActivity()
                MenuItens.changePassword -> startChangePassActivity()
                MenuItens.editSocialNetwork -> startEditSocialActivity()
                MenuItens.myEmbassy -> startMyEmbassyActivity()
                MenuItens.myEnrolledEvents -> startEnrolledEventsActivity()
                MenuItens.myFavoriteEvents -> startFavoriteEventsActivity()
                MenuItens.newEvent -> startCreateEventsActivity()
                MenuItens.sendInvites -> startInvitesActivity()
                MenuItens.sentEmbassyPhotos -> startUserProfileActivity()
                MenuItens.setPrivacy -> startSetPrivacyActivity()
                MenuItens.policyPrivacy -> startPrivacyActivity()
                MenuItens.embassyList -> startUserProfileActivity()
                MenuItens.aboutEmbassy -> startAboutEmbassiesActivity()
                MenuItens.abaoutApp -> startAboutAppActivity()
                MenuItens.suggestFeatures -> startSuggestsActivity()
                MenuItens.rateApp -> startAppRateActivity()
                MenuItens.sendUsMessage -> startSendMessageActivity()
                MenuItens.logout -> logout()
            }
        }

        mMenuList.onItemClickListener = listViewListener
    }

    private fun startCheckAuthActivity() {
        val intent: Intent = Intent(mContext, CheckAuthActivity::class.java)
        startActivity(intent)
    }

    private fun startUserProfileActivity() {
        val intent: Intent = Intent(mContext, UserProfileActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startEditProfileActivity() {
        val intent: Intent = Intent(mContext, EditProfileActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startChangeProfilePhotoActivity() {
        val intent: Intent = Intent(mContext, ChangeProfilePhotoActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startChangePassActivity() {
        val intent: Intent = Intent(mContext, ChangePassActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startEditSocialActivity() {
        val intent: Intent = Intent(mContext, EditSocialActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
    }

    private fun startMyEmbassyActivity() {
        val intent: Intent = Intent(mContext, MyEmbassyActivity::class.java)
        startActivity(intent)
    }

    private fun startSetPrivacyActivity() {
        val intent: Intent = Intent(mContext, SetPrivacyActivity::class.java)
        startActivity(intent)
    }

    private fun startPrivacyActivity() {
        val intent: Intent = Intent(mContext, PrivacyActivity::class.java)
        startActivity(intent)
    }

    private fun startEnrolledEventsActivity() {
        val intent: Intent = Intent(mContext, EnrolledEventsActivity::class.java)
        startActivity(intent)
    }

    private fun startFavoriteEventsActivity() {
        val intent: Intent = Intent(mContext, FavoriteEventsActivity::class.java)
        startActivity(intent)
    }

    private fun startCreateEventsActivity() {
        val intent: Intent = Intent(mContext, CreateEventActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startInvitesActivity() {
        val intent: Intent = Intent(mContext, InvitesActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startAboutEmbassiesActivity() {
        val intent: Intent = Intent(mContext, AboutEmbassiesActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutAppActivity() {
        val intent: Intent = Intent(mContext, AboutAppActivity::class.java)
        startActivity(intent)
    }

    private fun startSuggestsActivity() {
        val intent: Intent = Intent(mContext, SuggestsActivity::class.java)
        startActivity(intent)
    }

    private fun startAppRateActivity() {
        val intent: Intent = Intent(mContext, AppRateActivity::class.java)
        startActivity(intent)
    }

    private fun startSendMessageActivity() {
        val intent: Intent = Intent(mContext, SendMessageActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        mAuth.signOut()
        startCheckAuthActivity()
        activity?.finish()
    }


}
