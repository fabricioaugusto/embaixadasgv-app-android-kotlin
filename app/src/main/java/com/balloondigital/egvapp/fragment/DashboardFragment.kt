package com.balloondigital.egvapp.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Dashboard.EmbassyMembersActivity
import com.balloondigital.egvapp.activity.Dashboard.EmbassyPhotosActivity
import com.balloondigital.egvapp.activity.Single.EventProfileActivity
import com.balloondigital.egvapp.api.MyFirebase
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
class DashboardFragment : Fragment(), View.OnClickListener {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mAuth = MyFirebase.auth()
        mContext = view.context
        mDatabase = MyFirebase.database()
        mColletions = MyFirebase.COLLECTIONS
        mBtDashboardMembers = view.findViewById(R.id.btDashboardMembers)
        mBtDashboardPhotos = view.findViewById(R.id.btDashboardPhotos)
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

        if(id == R.id.layoutDashboardEvent) {
            startSingleEventActivity(mEvent)
        }
    }


    private fun setListeners() {
        mBtDashboardMembers.setOnClickListener(this)
        mBtDashboardPhotos.setOnClickListener(this)
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
        val intent: Intent = Intent(mContext, EmbassyPhotosActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startEmbassyMembersActivity() {
        val intent: Intent = Intent(mContext, EmbassyMembersActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }

    private fun startSingleEventActivity(event: Event) {
        val intent: Intent = Intent(mContext, EventProfileActivity::class.java)
        intent.putExtra("eventId", event.id)
        intent.putExtra("placeLat", event.lat)
        intent.putExtra("placeLng", event.long)
        intent.putExtra("placeName", event.place)
        startActivity(intent)
    }
}
