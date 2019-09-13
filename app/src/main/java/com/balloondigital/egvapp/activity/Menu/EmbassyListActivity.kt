package com.balloondigital.egvapp.activity.Menu

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Single.SingleEmbassyActivity
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.adapter.EmbassyListAdapter
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_embassy_members.*
import org.json.JSONArray

class EmbassyListActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnClickListener,
    SearchView.OnCloseListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mAdapter: EmbassyListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mListEmbassy: MutableList<Embassy>
    private lateinit var mListFiltered: MutableList<Embassy>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embassy_list)

        supportActionBar!!.title = "Lista das embaixadas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()
        mListEmbassy = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = findViewById(R.id.recyclerEmbassyList)

        setListeners()
        setRecyclerView()
        getListUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_users_toolbar, menu)
        val searchItem = menu?.findItem(R.id.bar_search)
        mSearchView = searchItem?.actionView as SearchView
        mSearchView.setOnQueryTextListener(this)
        mSearchView.setOnCloseListener(this)
        mSearchView.setOnSearchClickListener(this)
        return super.onCreateOptionsMenu(menu)
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

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClose(): Boolean {
        return false
    }

    private fun setListeners() {

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
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {embassy, pos -> startSingleEmbassyActivity(embassy)}
    }

    private fun setSearchRecyclerView(listEmbassy: MutableList<Embassy>) {

        mAdapter = EmbassyListAdapter(listEmbassy)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {embassy, pos -> startSingleEmbassyActivity(embassy)}
    }

    private fun startSingleEmbassyActivity(singleEmbassy: Embassy) {

        val intent: Intent = Intent(this, SingleEmbassyActivity::class.java)
        intent.putExtra("embassyID", singleEmbassy.id)
        startActivity(intent)
    }
}
