package com.balloondigital.egvapp.fragment.BottomNav.dashboard


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.EventProfileActivity
import com.balloondigital.egvapp.adapter.EventListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.BottomNav.agenda.SingleEventFragment
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_embassy_agenda.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EmbassyAgendaFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mEventList: MutableList<Event>
    private lateinit var mAdapter: EventListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_embassy_agenda, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mContext = view.context
        mDatabase = MyFirebase.database()
        mEventList = mutableListOf()
        mRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        mSwipeLayoutFeed.setOnRefreshListener { getEventList() }

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btBackPress.setOnClickListener(this)
        getEventList()
        setRecyclerView(mEventList)
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    private fun getEventList() {

        val today = Date()
        val timestamp = com.google.firebase.Timestamp(today)

        mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
            .whereEqualTo("embassy_id", mUser.embassy_id)
            .whereGreaterThan("date", timestamp)
            .orderBy("date", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

                mEventList.clear()

                if(documentSnapshot.documents.size > 0) {
                    for(document in documentSnapshot) {
                        val event: Event? = document.toObject(Event::class.java)
                        if(event != null) {
                            mEventList.add(event)
                        }
                    }
                } else {
                    txtEmbassyAgendaNoEvent.isGone = false
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
            }.addOnFailureListener {
                Log.d("EGVAPPLOGAGENDA", it.message.toString())
            }

    }


    private fun setRecyclerView(events: MutableList<Event>) {

        mAdapter = EventListAdapter(events)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_event)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                event -> startSingleEventActivity(event)
        }
    }

    private fun startSingleEventActivity(event: Event) {
        val lat = event.lat
        val long = event.long

        val bundle = Bundle()
        bundle.putString("eventId", event.id)
        bundle.putString("placeName", event.place)
        bundle.putInt("rootViewer", R.id.dashboardViewPager)

        if(lat != null && long != null) {
            bundle.putDouble("placeLat", lat)
            bundle.putDouble("placeLng", long)
        }

        val nextFrag = SingleEventFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "${R.id.dashboardViewPager}:singleEvent")
            .addToBackStack(null)
            .commit()
    }

}
