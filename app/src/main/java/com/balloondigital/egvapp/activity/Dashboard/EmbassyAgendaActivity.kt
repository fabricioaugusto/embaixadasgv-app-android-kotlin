package com.balloondigital.egvapp.activity.Dashboard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.EventProfileActivity
import com.balloondigital.egvapp.adapter.EventListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_embassy_agenda.*
import java.util.*

class EmbassyAgendaActivity : AppCompatActivity() {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mEventList: MutableList<Event>
    private lateinit var mAdapter: EventListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embassy_agenda)

        supportActionBar!!.title = "Agenda da embaixada"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mEventList = mutableListOf()
        mRecyclerView = findViewById(R.id.eventsRecyclerView)
        mSwipeLayoutFeed = findViewById(R.id.swipeLayoutFeed)

        mSwipeLayoutFeed.setOnRefreshListener { getEventList() }

        getEventList()
        setRecyclerView(mEventList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> true
        }
    }

    private fun getEventList() {

        val today = Date()
        val timestamp = com.google.firebase.Timestamp(today)

        mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
            .whereEqualTo("embassy_id", mUser.embassy_id)
            .whereGreaterThan("date", timestamp)
            .orderBy("date", Query.Direction.DESCENDING)
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
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_event)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                event -> startSingleEventActivity(event)
        }
    }

    private fun startSingleEventActivity(event: Event) {
        val intent: Intent = Intent(this, EventProfileActivity::class.java)
        intent.putExtra("eventId", event.id)
        intent.putExtra("placeLat", event.lat)
        intent.putExtra("placeLng", event.long)
        intent.putExtra("placeName", event.place)
        startActivity(intent)
    }
}
