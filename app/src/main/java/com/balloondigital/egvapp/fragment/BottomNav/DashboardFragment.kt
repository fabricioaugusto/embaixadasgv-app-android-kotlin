package com.balloondigital.egvapp.fragment.BottomNav


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
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Dashboard.EmbassyAgendaActivity
import com.balloondigital.egvapp.activity.Dashboard.EmbassyMembersActivity
import com.balloondigital.egvapp.activity.Dashboard.EmbassyPhotosActivity
import com.balloondigital.egvapp.activity.Single.EventProfileActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.BottomNav.agenda.ListEventsFragment
import com.balloondigital.egvapp.fragment.BottomNav.dashboard.DashboardPanelFragment
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
class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val nextFrag = DashboardPanelFragment()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.dashboardViewPager, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()

        // Inflate the layout for this fragment
        return view
    }


}
