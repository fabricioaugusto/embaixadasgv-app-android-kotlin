package com.balloondigital.egvapp.fragment.BottomNav


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.dashboard.DashboardPanelFragment

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
        val manager = activity!!.supportFragmentManager

        if(manager.findFragmentByTag("rootDashboardFragment") == null) {
            manager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "rootDashboardFragment")
            .addToBackStack(null)
            .commit()
        }

        // Inflate the layout for this fragment
        return view
    }


}
