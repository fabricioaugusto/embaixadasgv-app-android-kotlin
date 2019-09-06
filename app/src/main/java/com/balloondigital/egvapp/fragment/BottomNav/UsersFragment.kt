package com.balloondigital.egvapp.fragment.BottomNav


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.BottomNav.search.ListUsersFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UsersFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_users, container, false)

        val nextFrag = ListUsersFragment()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.searchViewPager, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()

        return view
    }

}
