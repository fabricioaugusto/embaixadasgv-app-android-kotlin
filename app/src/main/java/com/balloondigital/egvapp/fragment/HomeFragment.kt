package com.balloondigital.egvapp.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.BottomNav.DashboardFragment
import com.balloondigital.egvapp.model.User
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    private lateinit var mUser: User
    private lateinit var mFragmentAdapter: FragmentPagerItemAdapter
    private lateinit var mHighlightsFragment: Fragment
    private lateinit var mContext: Context
    private lateinit var mViewPager: ViewPager
    private lateinit var mViewPagerTab: SmartTabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        mContext = view.context
        mViewPager = view.findViewById(R.id.viewpager)
        mViewPagerTab = view.findViewById(R.id.viewpagertab)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLogFeed", mUser.toString())
        }

        mHighlightsFragment = HighlightsFragment()

        mFragmentAdapter = FragmentPagerItemAdapter(
            activity?.supportFragmentManager,
            FragmentPagerItems.with(mContext)
                .add("Painel", DashboardFragment::class.java)
                .add("Destaques", HighlightsFragment::class.java)
                .add("Info", InformativeFragment::class.java)
                .create())

        mViewPager.adapter = mFragmentAdapter
        mViewPagerTab.setViewPager(mViewPager)

        return view
    }


}
