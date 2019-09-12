package com.balloondigital.egvapp.fragment.BottomNav.menu


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.SingleEmbassyActivity
import com.balloondigital.egvapp.adapter.EmbassyListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_embassy.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListEmbassyFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener,
    SearchView.OnCloseListener {

    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: EmbassyListAdapter
    private lateinit var mToolbar: Toolbar
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mListEmbassy: MutableList<Embassy>
    private lateinit var mListFiltered: MutableList<Embassy>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_list_embassy, container, false)

        mToolbar = view.findViewById(R.id.listEmbassyToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mContext = view.context
        mDatabase = MyFirebase.database()
        mListEmbassy = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = view.findViewById(R.id.recyclerEmbassyList)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setRecyclerView()
        getListUsers()

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
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClose(): Boolean {
        return false
    }

    private fun setListeners() {

        btBackPress.setOnClickListener(this)
    }

    private fun searchEmbassy(str: String) {

        val query = str.toLowerCase()
        val mSearchList: List<Embassy> = mListEmbassy.filter {it.name.toLowerCase().contains(query)
                || it.city.toLowerCase().contains(query)
                || it.state.toLowerCase().contains(query)}

        setSearchRecyclerView(mSearchList.toMutableList())
    }

    private fun getListUsers() {

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .whereEqualTo("status", "active")
            .orderBy("state")
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    val embassy: Embassy? = document.toObject(Embassy::class.java)
                    if(embassy != null) {
                        mListEmbassy.add(embassy)
                    }
                }
                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
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

        mAdapter.onItemClick = {embassy -> startSingleEmbassyActivity(embassy)}
    }

    private fun setSearchRecyclerView(listEmbassy: MutableList<Embassy>) {

        mAdapter = EmbassyListAdapter(listEmbassy)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {embassy -> startSingleEmbassyActivity(embassy)}
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

}
