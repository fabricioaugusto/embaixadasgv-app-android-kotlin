package com.balloondigital.egvapp.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.balloondigital.egvapp.R
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    private lateinit var mFragmentAdapter: FragmentPagerItemAdapter
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        mContext = view.context

        mFragmentAdapter = FragmentPagerItemAdapter(
            activity?.supportFragmentManager,
            FragmentPagerItems.with(mContext)
                .add("Painel", DashboardFragment::class.java)
                .add("Destaques", HighlightsFragment::class.java)
                .add("Info", InformativeFragment::class.java)
                .create())

        viewpager.adapter = mFragmentAdapter
        viewpagertab.setViewPager(viewpager)

        return view
    }


}
