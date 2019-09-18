package com.balloondigital.egvapp.fragment.menu


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Create.CreateEventActivity
import com.balloondigital.egvapp.activity.Edit.EditEventActivity
import com.balloondigital.egvapp.adapter.*
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.agenda.ListEventsFragment
import com.balloondigital.egvapp.fragment.agenda.SingleEventFragment
import com.balloondigital.egvapp.fragment.dashboard.DashboardPanelFragment
import com.balloondigital.egvapp.model.*
import com.balloondigital.egvapp.model.MenuItem
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_manage_events.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ManageEventsFragment : Fragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mCurrentEvent: Event
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mEmbassyID: String
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mEventList: MutableList<Event>
    private lateinit var mAdapter: EventManagerListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mAdapterDialog: ManageItemsDialogAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private val EVENT_REQUEST_CODE: Int = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_manage_events, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
            mUser = bundle.getSerializable("user") as User
        }

        mToolbar = view.findViewById(R.id.managerEventToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mDatabase = MyFirebase.database()
        mEventList = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.rvManagerEvents)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        val menuList: List<MenuItem> = listOf(
            MenuItem("Visualizar", "item", R.drawable.ic_visibility_black),
            MenuItem("Editar", "item", R.drawable.ic_edit_black),
            MenuItem("Excluir", "item", R.drawable.ic_delete_black)
        )

        mAdapterDialog = ManageItemsDialogAdapter(mContext, false, 3, menuList)

        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getEventList() })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        getEventList()
        setRecyclerView(mEventList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_manager_event_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_create_event -> {
                startCreateEventActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {
            startSingleEventActivity()
        }

        if(position == 1) {
            startEditEventActivity()
        }

        if(position == 2) {
            confirmDeleteDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

             if(requestCode == EVENT_REQUEST_CODE) {
                if(data != null) {
                    getEventList()
                    updateEventLists()
                }
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            val status = Autocomplete.getStatusFromIntent(data!!)
            Log.i("GooglePlaceLog", status.statusMessage)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    private fun setListeners() {
        btBackPress.setOnClickListener(this)
    }

    private fun getEventList() {

        mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
            .whereEqualTo("embassy_id", mEmbassyID)
            .orderBy("date", Query.Direction.ASCENDING)
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
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
            }
    }


    private fun setRecyclerView(events: MutableList<Event>) {

        mAdapter = EventManagerListAdapter(events)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_event)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                event ->
            mCurrentEvent = event
            setManageItemsDialog()
        }
    }

    private fun startSingleEventActivity() {

        val lat = mCurrentEvent.lat
        val long = mCurrentEvent.long

        val bundle = Bundle()
        bundle.putString("eventId", mCurrentEvent.id)
        bundle.putString("placeName", mCurrentEvent.place)
        bundle.putInt("rootViewer", R.id.agendaViewPager)

        if(lat != null && long != null) {
            bundle.putDouble("placeLat", lat)
            bundle.putDouble("placeLng", long)
        }

        val nextFrag = SingleEventFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleEvent")
            .addToBackStack(null)
            .commit()
    }

    private fun startCreateEventActivity() {
        val intent: Intent = Intent(mContext, CreateEventActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, EVENT_REQUEST_CODE)
    }

    private fun startEditEventActivity() {
        val intent: Intent = Intent(mContext, EditEventActivity::class.java)
        intent.putExtra("eventID", mCurrentEvent.id)
        startActivityForResult(intent, EVENT_REQUEST_CODE)
    }

    private fun setManageItemsDialog() {

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(mContext)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapterDialog
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setPadding(16, 32, 16, 32)
            dialogBuilder.setGravity(Gravity.CENTER)
            mCPDialog = dialogBuilder.create()
            mCPDialog.show()
        }
    }

    private fun confirmDeleteDialog() {
        AlertDialog.Builder(mContext)
            .setIcon(R.drawable.ic_warning_yellow)
            .setTitle("Excluir evento")
            .setMessage("Tem certeza que deseja excluir este evento?")
            .setPositiveButton("Sim") { dialog, which ->
                mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
                    .document(mCurrentEvent.id)
                    .delete()
                    .addOnCompleteListener {
                        getEventList()
                        updateEventLists()
                    }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun updateEventLists() {

        val manager = activity!!.supportFragmentManager
        val eventListfragment: Fragment? = manager.findFragmentByTag("rootAgendaFragment")
        val dashboardPanelfragment: Fragment? = manager.findFragmentByTag("rootDashboardFragment")

        if(eventListfragment != null && eventListfragment.isVisible) {
            val eventList: ListEventsFragment = eventListfragment as ListEventsFragment
            eventList.refreshEvenList()
        }

        if(dashboardPanelfragment != null && dashboardPanelfragment.isVisible) {
            val dashboardPanel: DashboardPanelFragment = dashboardPanelfragment as DashboardPanelFragment
            dashboardPanel.refreshEvent()
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }
}
