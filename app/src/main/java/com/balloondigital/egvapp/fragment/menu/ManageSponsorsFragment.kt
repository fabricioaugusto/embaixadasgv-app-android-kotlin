package com.balloondigital.egvapp.fragment.menu


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
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Create.CreateEventActivity
import com.balloondigital.egvapp.activity.Edit.EditEventActivity
import com.balloondigital.egvapp.activity.Menu.SelectSponsorActivity
import com.balloondigital.egvapp.adapter.EventManagerListAdapter
import com.balloondigital.egvapp.adapter.ManageItemsDialogAdapter
import com.balloondigital.egvapp.adapter.SponsorListAdapter
import com.balloondigital.egvapp.adapter.SponsorManagerListAdapter
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
import kotlinx.android.synthetic.main.fragment_manage_sponsors.*
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ManageSponsorsFragment : Fragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mCurrentSponsor: EmbassySponsor
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mEmbassyID: String
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mEmbassySponsorList: MutableList<EmbassySponsor>
    private lateinit var mSponsorList: MutableList<Sponsor>
    private lateinit var mListEmbassy: MutableList<BasicEmbassy>
    private lateinit var mAdapter: SponsorListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mAdapterDialog: ManageItemsDialogAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private val SPONSOR_REQUEST_CODE: Int = 200
    private var mIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_manage_sponsors, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mEmbassyID = bundle.getString("embassyID", "")
            mUser = bundle.getSerializable("user") as User
        }

        mToolbar = view.findViewById(R.id.manageSponsorsToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mDatabase = MyFirebase.database()
        mSponsorList = mutableListOf()
        mListEmbassy = mutableListOf()
        mEmbassySponsorList = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.rvManageSponsors)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        val menuList: List<MenuItem> = listOf(
            MenuItem("Visualizar", "item", R.drawable.ic_visibility_black),
            MenuItem("Editar", "item", R.drawable.ic_edit_black),
            MenuItem("Excluir", "item", R.drawable.ic_delete_black)
        )

        mAdapterDialog = ManageItemsDialogAdapter(mContext, false, 3, menuList)

        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getSponsorList() })

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mAdapter.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        getSponsorList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_manager_event_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_create_event -> {
                startSelectSponsorActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {

        }

        if(position == 1) {
        }

        if(position == 2) {
            confirmDeleteDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == SPONSOR_REQUEST_CODE) {
                if(data != null) {
                    getSponsorList()
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

    private fun getSponsorList() {

        mDatabase.collection(MyFirebase.COLLECTIONS.SPONSORS)
            .orderBy("name", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

                mSponsorList.clear()
                mEmbassySponsorList.clear()

                if(documentSnapshot != null) {
                    if(documentSnapshot.size() > 0) {
                        for(document in documentSnapshot) {
                            val embassySponsor: EmbassySponsor? = document.toObject(EmbassySponsor::class.java)
                            if(embassySponsor != null) {
                                embassySponsor.id = document.id
                                mEmbassySponsorList.add(embassySponsor)
                            }
                        }
                        makeToast(mEmbassySponsorList.size.toString())
                        setSponsorList()
                    }
                }


            }
    }

    private fun getEmbassyList(embassySponsor: EmbassySponsor) {

        mListEmbassy.clear()

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .whereEqualTo("embassySponsor.id", embassySponsor.id)
            .get()
            .addOnSuccessListener {querySnapshot ->
                if(querySnapshot.size() > 0) {

                    for (document in querySnapshot) {
                        val embassy: BasicEmbassy? = document.toObject(BasicEmbassy::class.java)
                        if(embassy != null) {

                            embassy.id = document.id
                            mListEmbassy.add(embassy)
                        }
                    }

                }
                val sponsor: Sponsor = Sponsor(embassySponsor.name, mListEmbassy.toList())
                mSponsorList.add(sponsor)
                mIndex += 1
                setSponsorList()

                //mSkeletonScreen.hide()
            }
    }

    private fun getEmbassyWithoutSponsorList() {

        mListEmbassy.clear()

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .whereEqualTo("embassySponsor", null)
            .get()
            .addOnSuccessListener {querySnapshot ->
                if(querySnapshot.size() > 0) {

                    for (document in querySnapshot) {
                        val embassy: BasicEmbassy? = document.toObject(BasicEmbassy::class.java)
                        if(embassy != null) {

                            embassy.id = document.id
                            mListEmbassy.add(embassy)
                        }
                    }

                }
                val sponsor: Sponsor = Sponsor("Embaixadas sem padrinhos", mListEmbassy.toList())
                mSponsorList.add(sponsor)

                layoutMSponsorsLoading.isGone = true
                setRecyclerView(mSponsorList)
                //mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false


                //mSkeletonScreen.hide()
            }
    }

    private fun setSponsorList() {
        val count: Double = (mIndex/(mEmbassySponsorList.size+1).toDouble())*100
        txtMSponsorPercent.text = "${count.roundToInt()}%"
        if(mIndex < mEmbassySponsorList.size) {
            getEmbassyList(mEmbassySponsorList[mIndex])
        } else {
            getEmbassyWithoutSponsorList()
        }
    }


    private fun setRecyclerView(sponsors: MutableList<Sponsor>) {

        mAdapter = SponsorListAdapter(sponsors)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = mAdapter

        /*mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()*/

        mAdapter.onItemClick = {
                embassy -> startSingleEmbassyFragment(embassy)
        }
    }

    private fun startSingleEmbassyFragment(embassy: BasicEmbassy) {
        val bundle = Bundle()
        bundle.putSerializable("embassyID", embassy.id)

        val nextFrag = MyEmbassyFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleEmbassy")
            .addToBackStack(null)
            .commit()
    }


    private fun startSelectSponsorActivity() {
        val intent: Intent = Intent(mContext, SelectSponsorActivity::class.java)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, SPONSOR_REQUEST_CODE)
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
            .setTitle("Remover padrinho")
            .setMessage("Tem certeza que deseja remover ${mCurrentSponsor.name} como padrinho?")
            .setPositiveButton("Sim") { dialog, which ->
                mDatabase.collection(MyFirebase.COLLECTIONS.SPONSORS)
                    .document(mCurrentSponsor.id)
                    .delete()
                    .addOnCompleteListener {
                        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
                            .document(mCurrentSponsor.user_id)
                            .update("sponsor", false)
                            .addOnSuccessListener {
                                getSponsorList()
                            }
                    }
            }
            .setNegativeButton("Não", null)
            .show()
    }



    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }
}
