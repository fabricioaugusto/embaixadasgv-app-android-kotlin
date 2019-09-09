package com.balloondigital.egvapp.fragment.BottomNav

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.fragment.BottomNav.feed.ListPostFragment
import com.balloondigital.egvapp.model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FeedFragment : Fragment() {

    private lateinit var mUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_feed, container, false)

        val nextFrag = ListPostFragment()

        val bundle: Bundle? = arguments
        if (bundle != null) {
            nextFrag.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.feedViewPager, nextFrag, "rootFeedFragment")
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
