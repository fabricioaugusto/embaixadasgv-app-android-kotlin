package com.balloondigital.egvapp.fragment.BottomNav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.menu.MenuListFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_menu, container, false)

        val nextFrag = MenuListFragment()
        val manager = activity!!.supportFragmentManager

        if(manager.findFragmentByTag("rootMenuFragment") == null) {
            val bundle: Bundle? = arguments
            if (bundle != null) {
                nextFrag.arguments = bundle
                manager.beginTransaction()
                    .add(R.id.menuViewPager, nextFrag, "rootMenuFragment")
                    .addToBackStack(null)
                    .commit()
            }
        }


        return view
    }
}
