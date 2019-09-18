package com.balloondigital.egvapp.fragment.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.search.SingleUserFragment
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_embassy_members.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EmbassyMembersFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUser: User
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mClient: Client
    private lateinit var mImgLogoToolbar: ImageView
    private lateinit var mIndex: Index
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mListUsers: MutableList<User>
    private lateinit var mListFiltered: MutableList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_embassy_members, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mContext = view.context
        mDatabase = MyFirebase.database()
        mListUsers = mutableListOf()
        mListFiltered = mutableListOf()
        mRecyclerView = view.findViewById(R.id.rvEmbassyUsers)
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mIndex = mClient.getIndex("users")

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setRecyclerView()
        getListUsers()
    }

    override fun onQueryTextChange(str: String): Boolean {
        Log.d("searchView", "Listening..$str")
        if(str.isNotEmpty()) {
            searchUser(str)
        } else {
            setSearchRecyclerView(mListUsers)
        }
        return true
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.bar_search) {
            mImgLogoToolbar.isGone = true
        }
        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setListeners() {
        btBackPress.setOnClickListener(this)
        svEmbassyUsers.isIconified = false
        svEmbassyUsers.setOnQueryTextListener(this)
    }

    private fun searchUser(str: String) {

        val query = str.toLowerCase()
        val mSearchList: List<User> = mListUsers.filter {it.name.toLowerCase().contains(query)}
        setSearchRecyclerView(mSearchList.toMutableList())
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
                mListUsers.remove(mUser)
                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }
    }

    private fun setRecyclerView() {

        mAdapter = UserListAdapter(mListUsers)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {user -> startUserProfileActivity(user)}
    }

    private fun setSearchRecyclerView(listUser: MutableList<User>) {

        mAdapter = UserListAdapter(listUser)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = mAdapter

        mAdapter.onItemClick = {user -> startUserProfileActivity(user)}
    }

    private fun startUserProfileActivity(singleUser: User) {

        val bundle = Bundle()
        bundle.putSerializable("user", singleUser)

        val nextFrag = SingleUserFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.dashboardViewPager, nextFrag, "singleEmbassyUser")
            .addToBackStack(null)
            .commit()
    }

}
