package com.balloondigital.egvapp.fragment


import android.app.Activity
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Menu.MenuActivity
import com.balloondigital.egvapp.activity.Single.SingleArticleActivity
import com.balloondigital.egvapp.activity.Single.SinglePostActivity
import com.balloondigital.egvapp.activity.Single.SingleThoughtActivity
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.adapter.PostListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.balloondigital.egvapp.model.PostLike
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity


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
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mPostList: MutableList<Post>
    private lateinit var mLikeList: MutableList<PostLike>
    private lateinit var mBtFeedMenu: ImageButton
    private var mAdapterPosition: Int = 0
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
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                if(data != null) {
                    val removed: Boolean = data.getBooleanExtra("removed", false)
                    if(removed) {
                        mPostList.removeAt(mAdapterPosition)
                        mAdapter.notifyItemRemoved(mAdapterPosition)
                        mAdapter.notifyItemRangeChanged(mAdapterPosition, mPostList.size)
                        mAdapter.notifyDataSetChanged()
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
    }

    private fun setListeners() {
        mBtFeedMenu.setOnClickListener(this)
        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getListPosts() })
    }

    private fun setRecyclerView() {

        mAdapter = PostListAdapter(mPostList)
        mAdapter.setHasStableIds(true)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mAdapter.onItemClick = {
            post, pos ->

            mAdapterPosition = pos

            if(post.type == "note") {
                startPostArticleActivity(post)
            }
            if(post.type == "post") {
                startPostPictureActivity(post)
            }
            if(post.type == "thought") {
                startPostToughtActivity(post)
            }
        }
    }

    private fun startPostArticleActivity(post: Post) {
        val intent: Intent = Intent(mContext, SingleArticleActivity::class.java)
        intent.putExtra("post_id", post.id)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, 100)
    }

    private fun startPostPictureActivity(post: Post) {
        val intent: Intent = Intent(mContext, SinglePostActivity::class.java)
        intent.putExtra("post_id", post.id)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, 100)
    }

    private fun startPostToughtActivity(post: Post) {
        val intent: Intent = Intent(mContext, SingleThoughtActivity::class.java)
        intent.putExtra("post_id", post.id)
        intent.putExtra("user", mUser)
        startActivityForResult(intent, 100)
    }

    private fun getListPosts() {

        mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
            .whereEqualTo("user_verified", false)
            .orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

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
                mSwipeLayoutFeed.isRefreshing = false
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
