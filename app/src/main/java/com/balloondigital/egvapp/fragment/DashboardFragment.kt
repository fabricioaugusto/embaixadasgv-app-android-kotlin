package com.balloondigital.egvapp.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.DateStr
import com.balloondigital.egvapp.model.Event
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
class DashboardFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mEvent: Event
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mColletions: MyFirebase.COLLECTIONS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()
        mColletions = MyFirebase.COLLECTIONS

        getNextEvent()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
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
}
