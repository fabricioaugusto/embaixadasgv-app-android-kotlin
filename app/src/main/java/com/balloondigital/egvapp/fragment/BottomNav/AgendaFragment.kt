package com.balloondigital.egvapp.fragment.BottomNav


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.BottomNav.agenda.ListEventsFragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AgendaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_agenda, container, false)

        val nextFrag = ListEventsFragment()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.agendaViewPager, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()

        return view
    }
}