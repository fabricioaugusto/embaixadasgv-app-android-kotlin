package com.balloondigital.egvapp.fragment.menu


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
import com.balloondigital.egvapp.activity.Edit.*
import com.balloondigital.egvapp.activity.Menu.*
import com.balloondigital.egvapp.adapter.MenuListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.agenda.ListEventsFragment
import com.balloondigital.egvapp.fragment.agenda.SingleEventFragment
import com.balloondigital.egvapp.fragment.dashboard.DashboardPanelFragment
import com.balloondigital.egvapp.fragment.dashboard.EmbassyPhotosFragment
import com.balloondigital.egvapp.fragment.search.SingleUserFragment
import com.balloondigital.egvapp.model.MenuItem
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.MenuItens
import com.balloondigital.egvapp.utils.MyApplication
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuListFragment : Fragment() {

    private lateinit var mUser: User
    private lateinit var mContext: Context
    private lateinit var mMenuList: ListView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mBundle: Bundle
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: MenuListAdapter
    private lateinit var mMenuItensList: List<MenuItem>
    private lateinit var mMenuSectionList: List<MenuItem>
    private val MENU_REQUEST_CODE: Int = 100
    private val CREATE_EVENT_REQUEST_CODE: Int = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_menu_list, container, false)

        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()
        mContext = view.context
        mMenuList = view.findViewById(R.id.listViewMenu)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mBundle = bundle
            mUser = bundle.getSerializable("user") as User
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserDetails()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == MENU_REQUEST_CODE) {
                    if(data != null) {
                        mUser = data.getSerializableExtra("user") as User
                        mAdapter = MenuListAdapter(mContext, mUser)
                        setListView()
                    }
                } else if(requestCode == CREATE_EVENT_REQUEST_CODE) {
                    if(data != null) {

                        val eventId = data.getStringExtra("eventId")
                        val placeName = data.getStringExtra("placeName")
                        val placeLat = data.getDoubleExtra("placeLat", 0.0)
                        val placeLng = data.getDoubleExtra("placeLng", 0.0)

                        updateEventLists()
                        startSingleEventActivity(eventId!!, placeName!!, placeLat, placeLng)

                    }
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }

    }

    private fun getUserDetails() {
        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(mUser.id)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if(user != null) {
                    mUser = user
                    mAdapter = MenuListAdapter(mContext, mUser)
                    setListView()
                }
            }
    }



    private fun setListView() {

        var menulist: List<MenuItem> = MenuItens.getAccountSection()

        if(mUser.leader) {
            menulist = menulist + MenuItens.getLeaderSection()
        }

        if(mUser.sponsor) {
            menulist = menulist + MenuItens.getSponsorSection()
        }

        if(mUser.manager) {
            menulist = menulist + MenuItens.getManagerSection()
        }

        menulist = menulist + MenuItens.getPrivacySection()
        menulist = menulist + MenuItens.getMoreOptionsSection()

        mMenuSectionList = menulist.filter { it.type == "section" }

        var sectionIndex = 0

        for(item in menulist) {
            if(item.type == "section") {
                mAdapter.addSectionHeaderItem(mMenuSectionList[sectionIndex])
                sectionIndex += 1
            } else {
                mAdapter.addItem(item)
            }
        }

        mMenuList.adapter = mAdapter

        val listViewListener = AdapterView.OnItemClickListener { adapter, view, pos, posLong ->

            when(menulist[pos].item_name) {
                MenuItens.profile -> startUserProfileActivity()
                MenuItens.editProfile -> startEditProfileActivity()
                MenuItens.changeProfilePhoto -> startChangeProfilePhotoActivity()
                MenuItens.changePassword -> startChangePassActivity()
                MenuItens.editSocialNetwork -> startEditSocialActivity()
                MenuItens.myEmbassy -> startMyEmbassyActivity()
                MenuItens.myEnrolledEvents -> startEnrolledEventsActivity()
                MenuItens.myFavoriteEvents -> startFavoriteEventsActivity()
                MenuItens.newEvent -> startManageEventsActivity()
                MenuItens.sendInvites -> startInvitesActivity()
                MenuItens.sentEmbassyPhotos -> startSendEmbassyPhotosActivity()
                MenuItens.editEmbassy -> startEditEmbassyActivity()
                MenuItens.embassyForApproval -> startEmbassyForApprovalActivity()
                MenuItens.invitationRequests -> startApprovalInvitationRequestsActivity()
                MenuItens.affiliatedEmbassies -> startAffiliatedEmbassiesFragment()
                MenuItens.manageSponsors -> startManageSponsorsActivity()
                MenuItens.manageInteresteds -> startManageInterestedsActivity()
                MenuItens.report -> startReportFragment()
                MenuItens.createBulletin -> startManageBulletinsActivity()
                MenuItens.sendNotifications -> startSendNotificationsActivity()
                MenuItens.setPrivacy -> startSetPrivacyActivity()
                MenuItens.policyPrivacy -> startPrivacyActivity()
                MenuItens.embassyList -> startEmbassyListActivity()
                MenuItens.aboutEmbassy -> startAboutEmbassiesActivity()
                MenuItens.aboutApp -> startAboutAppActivity()
                MenuItens.suggestFeatures -> startSuggestsActivity()
                MenuItens.rateApp -> startAppRateActivity()
                MenuItens.sendUsMessage -> startSendMessageActivity()
                MenuItens.logout -> logout()
            }
        }

        mMenuList.onItemClickListener = listViewListener
    }

    private fun updateEventLists() {

        val manager = activity!!.supportFragmentManager
        val eventListfragment: Fragment? = manager.findFragmentByTag("rootAgendaFragment")
        val dashboardPanelfragment: Fragment? = manager.findFragmentByTag("rootDashboardFragment")

        if(eventListfragment != null && eventListfragment.isVisible) {
            val eventList: ListEventsFragment = eventListfragment as ListEventsFragment
            eventList.refreshEvenList()
        }

        if(dashboardPanelfragment != null && dashboardPanelfragment.isVisible) {
            val dashboardPanel: DashboardPanelFragment = dashboardPanelfragment as DashboardPanelFragment
            dashboardPanel.refreshEvent()
        }
    }

    private fun startCheckAuthActivity() {
        val intent: Intent = Intent(mContext, CheckAuthActivity::class.java)
        startActivity(intent)
    }

    private fun startUserProfileActivity() {
        val bundle = Bundle()
        bundle.putSerializable("user", mUser)

        val nextFrag = SingleUserFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleUser")
            .addToBackStack(null)
            .commit()
    }

    private fun startSingleEventActivity(eventId: String, placeName: String, placeLat: Double, placeLng: Double) {

        val bundle = Bundle()

        bundle.putString("eventId", eventId)
        bundle.putString("placeName", placeName)
        bundle.putDouble("placeLat", placeLat)
        bundle.putDouble("placeLng", placeLng)
        bundle.putInt("rootViewer", R.id.menuViewPager)

        val nextFrag = SingleEventFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleEvent")
            .addToBackStack(null)
            .commit()
    }

    private fun startEditProfileActivity() {

        val intent: Intent = Intent(mContext, EditProfileActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, MENU_REQUEST_CODE)
        activity?.overridePendingTransition(0, 0)
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
        val bundle = Bundle()
        bundle.putSerializable("embassyID", mUser.embassy_id)

        val nextFrag = MyEmbassyFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleEmbassy")
            .addToBackStack(null)
            .commit()
    }

    private fun startSetPrivacyActivity() {
        val intent: Intent = Intent(mContext, SetPrivacyActivity::class.java)
        intent.putExtra("user", mUser)
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

    private fun startManageEventsActivity() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)
        bundle.putSerializable("user", mUser)

        val nextFrag = ManageEventsFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:manageEvents")
            .addToBackStack(null)
            .commit()
    }

    private fun startInvitesActivity() {
        val intent: Intent = Intent(mContext, InvitesActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startApprovalInvitationRequestsActivity() {

        val bundle = Bundle()
        bundle.putSerializable("user", mUser)

        val nextFrag = ApprovalInvitationRequestsFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:approvalInvitationRequests")
            .addToBackStack(null)
            .commit()
    }

    private fun startSendEmbassyPhotosActivity() {
        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)
        bundle.putSerializable("user", mUser)

        val nextFrag = ManagePhotosFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:managePhotos")
            .addToBackStack(null)
            .commit()
    }

    private fun startEditEmbassyActivity() {
        val intent: Intent = Intent(mContext, EditEmbassyActivity::class.java)
        intent.putExtra("embassyID", mUser.embassy_id)
        startActivity(intent)
    }


    private fun startAffiliatedEmbassiesFragment() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)
        bundle.putSerializable("user", mUser)

        val nextFrag = AffiliatedEmbassiesFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:affiliatedEmbassies")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyForApprovalActivity() {
        val intent: Intent = Intent(mContext, EmbassiesForApprovalActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startManageSponsorsActivity() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)
        bundle.putSerializable("user", mUser)

        val nextFrag = ManageSponsorsFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:manageSponsors")
            .addToBackStack(null)
            .commit()
    }

    private fun startManageInterestedsActivity() {

        val bundle = Bundle()
        bundle.putSerializable("user", mUser)

        val nextFrag = InterestedListFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:interestedList")
            .addToBackStack(null)
            .commit()
    }

    private fun startManageBulletinsActivity() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)
        bundle.putSerializable("user", mUser)

        val nextFrag = ManageBulletinFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:manageBulletins")
            .addToBackStack(null)
            .commit()
    }

    private fun startSendNotificationsActivity() {

        val intent: Intent = Intent(mContext, SendNotificationActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startReportFragment() {
        val bundle = Bundle()
        bundle.putSerializable("user", mUser)

        val nextFrag = ReportFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:report")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyListActivity() {

        val nextFrag = ListEmbassyFragment()

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:listEmbassy")
            .addToBackStack(null)
            .commit()
    }

    private fun startAboutEmbassiesActivity() {
        val intent: Intent = Intent(mContext, AboutEmbassiesActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutAppActivity() {
        val intent: Intent = Intent(mContext, AboutAppActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startSuggestsActivity() {
        val intent: Intent = Intent(mContext, SuggestsActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startAppRateActivity() {
        MyApplication.util.openExternalLink(mContext, MyApplication.const.urls.googlePlayUrl)
    }

    private fun startSendMessageActivity() {
        val intent: Intent = Intent(mContext, SendMessageActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun logout() {
        mAuth.signOut()
        startCheckAuthActivity()
        activity?.finish()
    }
}
