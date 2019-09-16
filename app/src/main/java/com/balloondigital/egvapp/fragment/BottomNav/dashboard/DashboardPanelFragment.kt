package com.balloondigital.egvapp.fragment.BottomNav.dashboard


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isGone
import cn.pedant.SweetAlert.SweetAlertDialog

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Dashboard.EmbassyAgendaActivity
import com.balloondigital.egvapp.activity.Dashboard.EmbassyMembersActivity
import com.balloondigital.egvapp.activity.Dashboard.EmbassyPhotosActivity
import com.balloondigital.egvapp.activity.Single.EventProfileActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.BottomNav.agenda.SingleEventFragment
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

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
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mColletions: MyFirebase.COLLECTIONS
    private lateinit var mLayoutNextEvent: LinearLayout
    private lateinit var mTxtMonthAbrDashboard: TextView
    private lateinit var mTxtDashboardDate: TextView
    private lateinit var mTxtDashboardTheme: TextView
    private lateinit var mTxtDashboardTime: TextView
    private lateinit var mTxtDashboardLocation: TextView
    private lateinit var mTxtDashboardNoEvent: TextView
    private lateinit var mProgressBarDashboard: ProgressBar
    private lateinit var mRootView: ScrollView
    private lateinit var mBtDashboardMembers: Button
    private lateinit var mBtDashboardPhotos: Button
    private lateinit var mBtDashboardEvents: Button
    private lateinit var mBtDashboardCloud: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_dashboard_panel, container, false)

        mAuth = MyFirebase.auth()
        mContext = view.context
        mDatabase = MyFirebase.database()
        mColletions = MyFirebase.COLLECTIONS
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

    private fun startEmbassyPhotosActivity() {

        val bundle = Bundle()
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
        bundle.putSerializable("user", mUser)

        val nextFrag = EmbassyMembersFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "embassyMembers")
            .addToBackStack(null)
            .commit()
    }

    private fun startEmbassyAgendaActivity() {

        val bundle = Bundle()
        bundle.putSerializable("user", mUser)

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

    fun refreshEvent() {
        Log.d("EGVAPPLOGAGENDA", "atualizou dashboard")
        getNextEvent()
    }
}
