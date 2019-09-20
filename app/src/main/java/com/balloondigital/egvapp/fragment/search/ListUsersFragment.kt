package com.balloondigital.egvapp.fragment.search


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_users.*
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListUsersFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mClient: Client
    private lateinit var mToolbar: Toolbar
    private lateinit var mLastDocument: DocumentSnapshot
    private lateinit var mLastDocumentRequested: DocumentSnapshot
    private lateinit var mImgLogoToolbar: ImageView
    private lateinit var mIndex: Index
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSearchView: SearchView
    private lateinit var mListUsers: MutableList<User>
    private var isPostsOver: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_list_users, container, false)

        mImgLogoToolbar = view.findViewById(R.id.imgLogoToolbar)
        mToolbar = view.findViewById(R.id.searchToolbar)
        mToolbar.title = ""

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        }

        setHasOptionsMenu(true)

        mDatabase = MyFirebase.database()
        mListUsers = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.usersRecyclerView)
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mIndex = mClient.getIndex("users")

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListUsers()
        setRecyclerView()

        val recyclerListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if(!isPostsOver) {
                        if(!::mLastDocumentRequested.isInitialized) {
                            mLastDocumentRequested = mLastDocument
                            loadMore()
                        } else {
                            if(mLastDocumentRequested != mLastDocument) {
                                mLastDocumentRequested = mLastDocument
                                loadMore()
                            }
                        }
                    }

                }
            }
        }

        mRecyclerView.addOnScrollListener(recyclerListener)
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    override fun onClose(): Boolean {
        mImgLogoToolbar.isGone = false
        return false
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun searchUser(query: String) {

        val query = Query(query)
            .setAttributesToRetrieve("name", "profile_img")
            .setHitsPerPage(10)
        mIndex.searchAsync(query) { obj, p1 ->
            if(obj != null) {
                mListUsers.clear()
                val listObj = obj.get("hits") as JSONArray
                for (i in 0 until listObj.length()) {
                    val user = User()
                    val userObj = listObj.getJSONObject(i)
                    user.name = userObj.getString("name")
                    user.profile_img = userObj.getString("profile_img")
                    mListUsers.add(user)
                }
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun getListUsers() {

        isPostsOver = false

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .limit(3)
            .get()
            .addOnSuccessListener {querySnapshot ->

                if(querySnapshot.size() > 0) {
                    mLastDocument = querySnapshot.documents[querySnapshot.size() - 1]
                    for (document in querySnapshot) {
                        val user: User? = document.toObject(User::class.java)
                        if(user != null) {
                            mListUsers.add(user)
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }

    }

    private fun loadMore() {

        pbLoadingMore.isGone = false

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .startAfter(mLastDocument)
            .limit(3)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if(querySnapshot.size() > 0) {

                    mLastDocumentRequested = mLastDocument
                    mLastDocument = querySnapshot.documents[querySnapshot.size() - 1]

                    for (document in querySnapshot) {
                        val user: User? = document.toObject(User::class.java)
                        if(user != null) {
                            mListUsers.add(user)
                        }
                    }
                    mAdapter.notifyDataSetChanged()
                    pbLoadingMore.isGone = true
                } else {
                    pbLoadingMore.isGone = true
                    isPostsOver = true
                }

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

    private fun startUserProfileActivity(singleUser: User) {

        val bundle = Bundle()
        bundle.putSerializable("user", singleUser)

        val nextFrag = SingleUserFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.searchViewPager, nextFrag, "singleUser")
            .addToBackStack(null)
            .commit()
    }
}
