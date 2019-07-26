package com.balloondigital.egvapp.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.MenuActivity
import com.balloondigital.egvapp.activity.UserProfileActivity
import com.balloondigital.egvapp.model.User
import kotlinx.android.synthetic.main.fragment_feed.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FeedFragment : Fragment(), View.OnClickListener {

    private lateinit var mBtFeedMenu: ImageButton
    private lateinit var mContext: Context
    private lateinit var mUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_feed, container, false)

        mContext = view.context
        mBtFeedMenu = view.findViewById(R.id.btFeedMenu)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLogFeed", mUser.toString())
        }

        setListeners()

        return view
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btFeedMenu) {
            startMenuActivity()
        }
    }

    fun setListeners() {
        mBtFeedMenu.setOnClickListener(this)
    }

    fun startMenuActivity() {
        val intent: Intent = Intent(mContext, MenuActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }
}
