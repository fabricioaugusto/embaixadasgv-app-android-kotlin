package com.balloondigital.egvapp.activity.Dashboard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_embassy_members.*
import org.json.JSONArray

class EmbassyMembersActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUser: User
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mClient: Client
    private lateinit var mImgLogoToolbar: ImageView
    private lateinit var mIndex: Index
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mListUsers: MutableList<User>
    private lateinit var mListFiltered: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embassy_members)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mListUsers = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = findViewById(R.id.rvEmbassyUsers)
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mIndex = mClient.getIndex("users")

        setListeners()
        getListUsers()
    }

    override fun onQueryTextChange(str: String): Boolean {
        Log.d("searchView", "Listening..$str")
        if(str.isNotEmpty()) {
            searchUser(str)
        }
        return true
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.bar_search) {
            mImgLogoToolbar.isGone = true
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun setListeners() {
        svEmbassyUsers.setOnQueryTextListener(this)
    }

    private fun searchUser(str: String) {

        val fullList = mListUsers
        val query = str.toLowerCase()
        val mSearchList: List<User> = mListUsers.filter {it.name.toLowerCase().contains(query)}
        setRecyclerView(mSearchList.toMutableList())
    }

    private fun getListUsers() {

        Log.d("EGVAPPLOGUSERS", mUser.embassy_id.toString())

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .whereEqualTo("embassy_id", mUser.embassy.id)
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    val user: User? = document.toObject(User::class.java)
                    if(user != null) {
                        mListUsers.add(user)
                    }
                }
                setRecyclerView(mListUsers)
                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }
    }

    private fun setRecyclerView(listUser: MutableList<User>) {

        mAdapter = UserListAdapter(listUser)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {user -> startUserProfileActivity(user)}
    }

    private fun startUserProfileActivity(singleUser: User) {

        val intent: Intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra("user", singleUser)
        startActivity(intent)
    }
}
