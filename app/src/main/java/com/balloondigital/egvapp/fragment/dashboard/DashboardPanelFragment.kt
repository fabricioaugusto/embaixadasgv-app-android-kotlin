package com.balloondigital.egvapp.fragment.dashboard


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.BulletinManagerListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.agenda.SingleEventFragment
import com.balloondigital.egvapp.model.*
import com.balloondigital.egvapp.utils.Converters
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import java.util.*
import android.widget.TextView
import androidx.core.view.MenuItemCompat
import com.google.firebase.firestore.FieldValue


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DashboardPanelFragment : Fragment(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mContext: Context
    private lateinit var mToolbar: Toolbar
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mColletions: MyFirebase.COLLECTIONS
    private lateinit var mAdapter: BulletinManagerListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mBulletinList: MutableList<Bulletin>
    private lateinit var mLayoutNextEvent: LinearLayout
    private lateinit var mTxtMonthAbrDashboard: TextView
    private lateinit var mTxtDashboardDate: TextView
    private lateinit var mTxtDashboardTheme: TextView
    private lateinit var mTxtDashboardTime: TextView
    private lateinit var mTxtDashboardLocation: TextView
    private lateinit var mTxtDashboardNoEvent: TextView
    private lateinit var mProgressBarDashboard: ProgressBar
    private lateinit var mRootView: NestedScrollView
    private lateinit var mBtDashboardMembers: Button
    private lateinit var mBtDashboardPhotos: Button
    private lateinit var mBtDashboardEvents: Button
    private lateinit var mBtDashboardCloud: Button
    private lateinit var mNotificationBadge: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_dashboard_panel, container, false)


        mToolbar = view.findViewById(R.id.homeToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mAuth = MyFirebase.auth()
        mContext = view.context
        mDatabase = MyFirebase.database()
        mColletions = MyFirebase.COLLECTIONS
        mBulletinList = mutableListOf()
        mRecyclerView = view.findViewById(R.id.rvBulletinList)
        mBtDashboardCloud = view.findViewById(R.id.btDashboardCloud)
        mBtDashboardMembers = view.findViewById(R.id.btDashboardMembers)
        mBtDashboardPhotos = view.findViewById(R.id.btDashboardPhotos)
        mBtDashboardEvents = view.findViewById(R.id.btDashboardEvents)
        mLayoutNextEvent = view.findViewById(R.id.layoutNextEvent)
        mTxtMonthAbrDashboard = view.findViewById(R.id.txtMonthAbrDashboard)
        mTxtDashboardDate = view.findViewById(R.id.txtDashboardDate)
        mTxtDashboardTheme = view.findViewById(R.id.txtDashboardTheme)
        mTxtDashboardTime = view.findViewById(R.id.txtDashboardTime)
        mTxtDashboardLocation = view.findViewById(R.id.txtDashboardLocation)
        mProgressBarDashboard = view.findViewById(R.id.progressBarDashboard)
        mTxtDashboardNoEvent = view.findViewById(R.id.txtDashboardNoEvent)
        mRootView = view.findViewById(R.id.rootView)

        val currentUser = mAuth.currentUser
        if(currentUser != null) {
            getUserDetails(currentUser.uid)
        }

        setListeners()

        Log.d("EGVAPPLOGAGENDA", tag.toString())
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        getBulletinList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_dashboard_toolbar, menu)

        val menuItem = menu.findItem(R.id.action_notification)

        val actionView = menuItem.actionView
        mNotificationBadge = actionView.findViewById(R.id.notification_badge)

        getNotifications()

        actionView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startNotificationsFragment()
            }
        })

        //setupBadge()

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_create_post -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btDashboardMembers) {
            startEmbassyMembersActivity()
        }

        if(id == R.id.btDashboardPhotos) {
            startEmbassyPhotosActivity()
        }

        if(id == R.id.btDashboardEvents) {
            startEmbassyAgendaActivity()
        }

        if(id == R.id.layoutNextEvent) {
            startSingleEventActivity(mEvent)
        }

        if(id == R.id.btDashboardCloud) {
            SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Em breve!")
                .setContentText("Este recurso será disponibilizado nas próximas atualizações, aguarde! :)")
                .show()
        }
    }


    private fun setListeners() {
        mBtDashboardMembers.setOnClickListener(this)
        mBtDashboardPhotos.setOnClickListener(this)
        mBtDashboardEvents.setOnClickListener(this)
        mBtDashboardCloud.setOnClickListener(this)
        mLayoutNextEvent.setOnClickListener(this)
    }

    private fun getNotifications() {

        val today = Date()
        val timestamp = com.google.firebase.Timestamp(today)

        mDatabase.collection(mColletions.NOTIFICATIONS)
            .whereGreaterThan("last_read_notification", timestamp)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->

                if(querySnapshot.documents.size > 0) {
                    mNotificationBadge.text = querySnapshot.documents.size.toString()
                } else {
                    mNotificationBadge.isGone = true
                }
            }
    }

    private fun getUserDetails(userId: String) {
        mDatabase.collection(mColletions.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if(user != null) {
                    mUser = user
                    getNextEvent()
                }
            }
    }

    private fun getNextEvent() {

        val today = Date()
        val timestamp = com.google.firebase.Timestamp(today)

        mDatabase.collection(mColletions.EVENTS)
            .whereEqualTo("embassy_id", mUser.embassy_id)
            .whereGreaterThan("date", timestamp)
            .limit(1)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                if(querySnapshot.size() > 0) {
                    for(document in querySnapshot) {
                        val event = document.toObject(Event::class.java)
                        if(event != null) {
                            mEvent = event
                            bindEventData()
                        }
                    }
                } else{
                    mTxtDashboardNoEvent.text = "No momento não há eventos previstos!"
                    mTxtDashboardNoEvent.isGone = false
                    mLayoutNextEvent.isGone = true
                    mProgressBarDashboard.isGone = true
                    mRootView.isGone = false
                }
            }.addOnFailureListener {
                Log.d("EGVAPPLOGAGENDA", it.message.toString())
            }
    }

    private fun bindEventData() {

        val dateStr =  Converters.dateToString(mEvent.date!!)
        mTxtMonthAbrDashboard.text = dateStr.monthAbr
        mTxtDashboardDate.text = dateStr.date
        mTxtDashboardTheme.text = mEvent.theme
        mTxtDashboardTime.text = "${dateStr.weekday} às ${dateStr.hours}:${dateStr.minutes}"
        mTxtDashboardLocation.text = "${mEvent.city}, ${mEvent.state_short}"



        mProgressBarDashboard.isGone = true
        mRootView.isGone = false
    }

    private fun getBulletinList() {

        mDatabase.collection(MyFirebase.COLLECTIONS.BULLETIN)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(3)
            .get().addOnSuccessListener { documentSnapshot ->

                mBulletinList.clear()

                if(documentSnapshot != null) {
                    if(documentSnapshot.size() > 0) {
                        for(document in documentSnapshot) {
                            val bulletin: Bulletin? = document.toObject(Bulletin::class.java)
                            if(bulletin != null) {
                                mBulletinList.add(bulletin)
                            }
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }
    }


    private fun setRecyclerView() {

        mAdapter = BulletinManagerListAdapter(mBulletinList)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_event)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                bulletin -> startSingleBulletinActivity(bulletin)

        }
    }

    private fun startNotificationsFragment() {

        val bundle = Bundle()
        bundle.putSerializable("user", mUser)

        val nextFrag = ListNotificationsFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "listNotifications")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyPhotosActivity() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)
        bundle.putSerializable("user", mUser)

        val nextFrag = EmbassyPhotosFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "embassyPhotos")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyMembersActivity() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)

        val nextFrag = EmbassyMembersFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "embassyMembers")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyAgendaActivity() {

        val bundle = Bundle()
        bundle.putString("embassyID", mUser.embassy_id)

        val nextFrag = EmbassyAgendaFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "embassyAgenda")
            .addToBackStack(null)
            .commit()
    }

    private fun startSingleEventActivity(event: Event) {
        val lat = event.lat
        val long = event.long

        val bundle = Bundle()
        bundle.putString("eventId", event.id)
        bundle.putString("placeName", event.place)
        bundle.putInt("rootViewer", R.id.dashboardViewPager)

        if(lat != null && long != null) {
            bundle.putDouble("placeLat", lat)
            bundle.putDouble("placeLng", long)
        }

        val nextFrag = SingleEventFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "${R.id.dashboardViewPager}:singleEvent")
            .addToBackStack(null)
            .commit()
    }

    private fun startSingleBulletinActivity(bulletin: Bulletin) {

        val bundle = Bundle()
        bundle.putString("bulletinID", bulletin.id)

        val nextFrag = SingleBulletinFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "${R.id.dashboardViewPager}:singleBulletin")
            .addToBackStack(null)
            .commit()
    }

    fun refreshEvent() {
        getNextEvent()
    }

    fun refreshBulletin() {
        getBulletinList()
    }
}
