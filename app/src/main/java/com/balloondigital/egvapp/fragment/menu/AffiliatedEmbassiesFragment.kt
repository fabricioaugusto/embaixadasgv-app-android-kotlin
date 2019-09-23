package com.balloondigital.egvapp.fragment.menu


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Menu.AffiliatesEmbassiesAwaitingActivity
import com.balloondigital.egvapp.activity.Menu.EmbassiesForApprovalActivity
import com.balloondigital.egvapp.adapter.EmbassyListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.EmbassySponsor
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_affiliated_embassies.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AffiliatedEmbassiesFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener,
    SearchView.OnCloseListener {


    private lateinit var mUser: User
    private lateinit var mSponsorID: String
    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: EmbassyListAdapter
    private lateinit var mToolbar: Toolbar
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mAwaitingListEmbassy: MutableList<Embassy>
    private lateinit var mListEmbassy: MutableList<Embassy>
    private lateinit var mListFiltered: MutableList<Embassy>
    private var isPostsOver: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_affiliated_embassies, container, false)


        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mToolbar = view.findViewById(R.id.listEmbassyToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mContext = view.context
        mDatabase = MyFirebase.database()
        mAwaitingListEmbassy = mutableListOf()
        mListEmbassy = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = view.findViewById(R.id.recyclerEmbassyList)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setRecyclerView()
        getSponsorId()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_users_toolbar, menu)
        val searchItem = menu.findItem(R.id.bar_search)
        mSearchView = searchItem?.actionView as SearchView
        mSearchView.setOnQueryTextListener(this)
        mSearchView.setOnCloseListener(this)
        mSearchView.setOnSearchClickListener(this)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_search -> {
                return true
            }
            else -> true
        }
    }

    override fun onQueryTextChange(str: String): Boolean {
        if(str.isNotEmpty()) {
            searchEmbassy(str)
        } else {
            setSearchRecyclerView(mListEmbassy)
        }
        return true
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }

        if(id == R.id.layoutAwaitingEmbassies) {
            startAffiliatesEmbassiesAwaitingActivity()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClose(): Boolean {
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == 300) {
                getListEmbassy()
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            val status = Autocomplete.getStatusFromIntent(data!!)
            Log.i("GooglePlaceLog", status.statusMessage)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

    private fun setListeners() {
        layoutAwaitingEmbassies.setOnClickListener(this)
        btBackPress.setOnClickListener(this)

    }

    private fun searchEmbassy(str: String) {

        val query = str.toLowerCase()
        val mSearchList: List<Embassy> = mListEmbassy.filter {it.name.toLowerCase().contains(query)
                || it.city.toLowerCase().contains(query)
                || it.state.toLowerCase().contains(query)}

        setSearchRecyclerView(mSearchList.toMutableList())
    }

    private fun getSponsorId() {
        mDatabase.collection(MyFirebase.COLLECTIONS.SPONSORS)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener {
                querySnapshot ->

                if(querySnapshot.documents.size > 0) {

                    mSponsorID = querySnapshot.documents[0].id
                    getListEmbassy()
                }
            }
    }

    private fun getListEmbassy() {

        mListEmbassy.clear()
        mAwaitingListEmbassy.clear()

        isPostsOver = false

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .whereEqualTo("embassySponsor.id", mSponsorID)
            .get()
            .addOnSuccessListener {querySnapshot ->
                if(querySnapshot.size() > 0) {
                    for (document in querySnapshot) {
                        val embassy: Embassy? = document.toObject(Embassy::class.java)
                        if(embassy != null) {
                            embassy.id = document.id

                            if(embassy.status == "active") {
                                mListEmbassy.add(embassy)
                            }

                            if(embassy.status == "awaiting") {
                                mAwaitingListEmbassy.add(embassy)
                            }

                        }
                    }
                }

                bindData()
                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }
    }

    private fun bindData() {

        if(mAwaitingListEmbassy.size > 0) {
            txtAwaitingEmbassies.text = mAwaitingListEmbassy.size.toString()
            layoutAwaitingEmbassies.isGone = false
        } else {
            layoutAwaitingEmbassies.isGone = true
        }

    }

    private fun setRecyclerView() {

        mAdapter = EmbassyListAdapter(mListEmbassy)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {embassy, pos -> startSingleEmbassyActivity(embassy)}
    }

    private fun setSearchRecyclerView(listEmbassy: MutableList<Embassy>) {

        mAdapter = EmbassyListAdapter(listEmbassy)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {embassy, pos -> startSingleEmbassyActivity(embassy)}
    }

    private fun startSingleEmbassyActivity(singleEmbassy: Embassy) {

        val bundle = Bundle()
        bundle.putSerializable("embassyID", singleEmbassy.id)

        val nextFrag = MyEmbassyFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.menuViewPager, nextFrag, "${R.id.menuViewPager}:singleEmbassy")
            .addToBackStack(null)
            .commit()
    }

    private fun startAffiliatesEmbassiesAwaitingActivity() {
        val intent: Intent = Intent(mContext, AffiliatesEmbassiesAwaitingActivity::class.java)
        intent.putExtra("user", mUser)
        intent.putExtra("sponsor_id", mSponsorID)
        startActivityForResult(intent, 300)

    }

}
