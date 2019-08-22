package com.balloondigital.egvapp.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Dashboard.EmbassyMembersActivity
import com.balloondigital.egvapp.activity.Dashboard.EmbassyPhotosActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*
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
    }


    private fun setListeners() {
        mBtDashboardMembers.setOnClickListener(this)
        mBtDashboardPhotos.setOnClickListener(this)
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
                            Log.d("EGVAPPLOGDASH", event.toString())
                            bindEventData()
                        }
                    }
                }
            }
    }

    private fun bindEventData() {

        val dateStr =  Converters.dateToString(mEvent.date!!)
        txtMonthAbrDashboard.text = dateStr.monthAbr
        txtDashboardDate.text = dateStr.date
        txtDashboardTheme.text = mEvent.theme
        txtDashboardTime.text = "${dateStr.weekday} às ${dateStr.hours}:${dateStr.minutes}"
        txtDashboardLocation.text = "${mEvent.city}, ${mEvent.state_short}"

        progressBarDashboard.isGone = true
        rootView.isGone = false
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
}
