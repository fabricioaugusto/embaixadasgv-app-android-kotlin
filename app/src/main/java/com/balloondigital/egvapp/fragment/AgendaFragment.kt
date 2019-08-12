package com.balloondigital.egvapp.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.EventProfileActivity
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.adapter.EventListAdapter
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AgendaFragment : Fragment() {

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
        val view: View = inflater.inflate(R.layout.fragment_agenda, container, false)

        mDatabase = MyFirebase.database()
        mEventList = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getEventList() })

        getEventList()
        setRecyclerView(mEventList)

        return view
    }

    private fun getEventList() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
            .orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

                mEventList.clear()

                if(documentSnapshot != null) {
                    for(document in documentSnapshot) {
                        val event: Event? = document.toObject(Event::class.java)
                        if(event != null) {
                            mEventList.add(event)
                        }
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
        val intent: Intent = Intent(mContext, EventProfileActivity::class.java)
        intent.putExtra("eventId", event.id)
        intent.putExtra("placeLat", event.lat)
        intent.putExtra("placeLng", event.long)
        intent.putExtra("placeName", event.place)
        startActivity(intent)
    }
}
