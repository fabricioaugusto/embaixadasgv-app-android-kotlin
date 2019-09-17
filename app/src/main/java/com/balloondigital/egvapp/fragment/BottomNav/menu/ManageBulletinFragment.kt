package com.balloondigital.egvapp.fragment.BottomNav.menu


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Create.CreateEventActivity
import com.balloondigital.egvapp.activity.Edit.EditEventActivity
import com.balloondigital.egvapp.adapter.BulletinManagerListAdapter
import com.balloondigital.egvapp.adapter.ManageItemsDialogAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.BottomNav.agenda.SingleEventFragment
import com.balloondigital.egvapp.fragment.BottomNav.dashboard.DashboardPanelFragment
import com.balloondigital.egvapp.model.Bulletin
import com.balloondigital.egvapp.model.MenuItem
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ManageBulletinFragment : Fragment(), OnItemClickListener {


    private lateinit var mUser: User
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mCurrentBulletin: Bulletin
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mBulletinList: MutableList<Bulletin>
    private lateinit var mAdapter: BulletinManagerListAdapter
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
        val view: View = inflater.inflate(R.layout.fragment_manage_bulletin, container, false)

        mToolbar = view.findViewById(R.id.managerBulletinToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mDatabase = MyFirebase.database()
        mBulletinList = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.rvManagerEvents)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        val menuList: List<MenuItem> = listOf(
            MenuItem("Visualizar", "item", R.drawable.ic_visibility_black),
            MenuItem("Editar", "item", R.drawable.ic_edit_black),
            MenuItem("Excluir", "item", R.drawable.ic_delete_black)
        )

        mAdapterDialog = ManageItemsDialogAdapter(mContext, false, 3, menuList)

        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getBulletinList() })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBulletinList()
        setRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_manager_event_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_create_event -> {
                startCreateBulletinActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {
            startSingleBulletinActivity()
        }

        if(position == 1) {
            startEditBulletinActivity()
        }

        if(position == 2) {
            confirmDeleteDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == EVENT_REQUEST_CODE) {
                if(data != null) {
                    getBulletinList()
                    updateBulletinLists()
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

    private fun getBulletinList() {

        mDatabase.collection(MyFirebase.COLLECTIONS.BULLETIN)
            .orderBy("date", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

                mBulletinList.clear()

                if(documentSnapshot != null) {
                    if(documentSnapshot.size() > 0) {
                        for(document in documentSnapshot) {
                            val bulletin: Bulletin? = document.toObject(Bulletin::class.java)
                            if(bulletin != null) {
                                mBulletinList.add(bulletin)
                            }
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
            }
    }


    private fun setRecyclerView() {

        mAdapter = BulletinManagerListAdapter(mBulletinList)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_event)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                bulletin ->
            mCurrentBulletin = bulletin
            setManageItemsDialog()
        }
    }

    private fun startSingleBulletinActivity() {

        val nextFrag = SingleEventFragment()

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleBulletin")
            .addToBackStack(null)
            .commit()
    }

    private fun startCreateBulletinActivity() {
        val intent: Intent = Intent(mContext, CreateEventActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, EVENT_REQUEST_CODE)
    }

    private fun startEditBulletinActivity() {
        val intent: Intent = Intent(mContext, EditEventActivity::class.java)
        intent.putExtra("eventID", mCurrentBulletin.id)
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
            .setTitle("Excluir informativo")
            .setMessage("Tem certeza que deseja excluir este informativo?")
            .setPositiveButton("Sim") { dialog, which ->
                mDatabase.collection(MyFirebase.COLLECTIONS.BULLETIN)
                    .document(mCurrentBulletin.id)
                    .delete()
                    .addOnCompleteListener {
                        getBulletinList()
                        updateBulletinLists()
                    }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun updateBulletinLists() {

        val manager = activity!!.supportFragmentManager
        val dashboardPanelfragment: Fragment? = manager.findFragmentByTag("rootDashboardFragment")

        if(dashboardPanelfragment != null && dashboardPanelfragment.isVisible) {
            val dashboardPanel: DashboardPanelFragment = dashboardPanelfragment as DashboardPanelFragment
            dashboardPanel.refreshEvent()
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }
}
