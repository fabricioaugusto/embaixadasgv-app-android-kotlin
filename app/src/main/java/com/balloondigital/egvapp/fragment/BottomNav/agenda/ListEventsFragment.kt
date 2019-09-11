package com.balloondigital.egvapp.fragment.BottomNav.agenda


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
import com.balloondigital.egvapp.model.Event
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_list_events.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListEventsFragment : Fragment() {

    private lateinit var mUser: Context
    private lateinit var mContext: Context
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
        // Inflate the layout for this fragment


        val view: View = inflater.inflate(R.layout.fragment_list_events, container, false)

        mDatabase = MyFirebase.database()
        mEventList = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getEventList() })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEventList()
        setRecyclerView(mEventList)
    }

    private fun getEventList() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
            .orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

                mEventList.clear()

                if(documentSnapshot != null) {
                    if(documentSnapshot.size() > 0) {
                        for(document in documentSnapshot) {
                            val event: Event? = document.toObject(Event::class.java)
                            if(event != null) {
                                mEventList.add(event)
                            }
                        }
                        layoutEmptyEvents.isGone = true
                    } else {
                        layoutEmptyEvents.isGone = false
                    }

                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
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

        if(lat != null && long != null) {
            bundle.putDouble("placeLat", lat)
            bundle.putDouble("placeLng", long)
        }

        val nextFrag = SingleEventFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.agendaViewPager, nextFrag, "singleEvent")
            .addToBackStack(null)
            .commit()
    }
}
