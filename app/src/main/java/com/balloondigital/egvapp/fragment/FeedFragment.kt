package com.balloondigital.egvapp.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.MenuActivity
import com.balloondigital.egvapp.activity.UserProfileActivity
import com.balloondigital.egvapp.adapter.PostListAdapter
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_feed.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FeedFragment : Fragment(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: PostListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPostList: MutableList<Post>
    private lateinit var mBtFeedMenu: ImageButton
    private lateinit var mUser: User
    private lateinit var mDbListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_feed, container, false)

        mContext = view.context
        mBtFeedMenu = view.findViewById(R.id.btFeedMenu)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLogFeed", mUser.toString())
        }
        mDatabase = MyFirebase.database()
        mPostList = mutableListOf()
        mRecyclerView = view.findViewById(R.id.postsRecyclerView)

        getListPosts()
        setRecyclerView()
        setListeners()

        return view
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btFeedMenu) {
            startMenuActivity()
        }
    }

    private fun setListeners() {
        mBtFeedMenu.setOnClickListener(this)
    }

    private fun setRecyclerView() {

        mAdapter = PostListAdapter(mPostList)
        mAdapter.setHasStableIds(true)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.setItemViewCacheSize(20)
        mAdapter.onItemClick = {
            post ->
        }

    }

    private fun getListPosts() {

        mDbListener = mDatabase.collection(MyFirebase.COLLECTIONS.POSTS).orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                mPostList.clear()

                if(documentSnapshot != null) {
                    for(document in documentSnapshot) {
                        val post: Post? = document.toObject(Post::class.java)
                        if(post != null) {
                            mPostList.add(post)
                        }
                    }
                }
                
                mAdapter.notifyDataSetChanged()
            }
    }

    private fun startUserProfileActivity(singleUser: User) {

        val intent: Intent = Intent(mContext, UserProfileActivity::class.java)
        intent.putExtra("user", singleUser)
        startActivity(intent)
    }

    fun startMenuActivity() {
        val intent: Intent = Intent(mContext, MenuActivity::class.java)
        intent.putExtra("user", mUser)
        startActivity(intent)
    }
}
