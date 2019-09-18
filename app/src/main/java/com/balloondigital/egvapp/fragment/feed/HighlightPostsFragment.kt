package com.balloondigital.egvapp.fragment.feed


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.PostListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.PostLike
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HighlightPostsFragment : Fragment() {


    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: PostListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mPostList: MutableList<Post>
    private var mAdapterPosition: Int = 0
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FRAGLIFECYCLE","Destaques onCreate")
        val manager = activity!!.supportFragmentManager
        val fragment: Fragment? = manager.findFragmentByTag("rootFeedFragment")
        val rootListPost: ListPostFragment = fragment as ListPostFragment
        Log.d("EGVAPPLOGVISIBLEFRAGS", this.tag.toString())
        rootListPost.setFragmentTags("HighlightPostsFragment", tag!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_highlight_posts, container, false)
        // Inflate the layout for this fragment


        mContext = view.context
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
            Log.d("FirebaseLogFeed", mUser.toString())
        }

        mDatabase = MyFirebase.database()
        mPostList = mutableListOf()
        mRecyclerView = view.findViewById(R.id.postsRecyclerView)

        getPostLikes()
        setListeners()

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            when(requestCode){
                100 -> {
                    if(data != null) {
                        val removed: Boolean = data.getBooleanExtra("removed", false)
                        if(removed) {
                            mPostList.removeAt(mAdapterPosition)
                            mAdapter.notifyItemRemoved(mAdapterPosition)
                            mAdapter.notifyItemRangeChanged(mAdapterPosition, mPostList.size)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
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

    fun updatePost(post: Post) {

        if(mPostList.any { p -> p.id == post.id }) {
            val list = mPostList.filter { p -> p.id == post.id }
            val pos = mPostList.indexOf(list[0])
            mPostList[pos] = post
            mAdapter.notifyItemChanged(pos)
        }

    }

    fun updateList() {
        getPostLikes()
    }

    private fun setListeners() {
        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getPostLikes() })
    }

    private fun setRecyclerView() {

        mAdapter = PostListAdapter(mPostList, mUser, activity!!)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.itemAnimator = null

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_post)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                post, pos ->

            mAdapterPosition = pos

            if(post.type == "note") {
                startSinglePost(post)
            }
            if(post.type == "post") {
                startSinglePost(post)
            }
            if(post.type == "thought") {
                startSinglePost(post)
            }
        }
    }


    private fun startSinglePost(post: Post) {
        val bundle = Bundle()

        bundle.putString("post_id", post.id)
        bundle.putString("frag_tag", this.tag)
        bundle.putString("frag_name", "HighlightPostsFragment")
        bundle.putSerializable("user", mUser)

        val nextFrag = SinglePostFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.feedViewPager, nextFrag, "singlePost")
            .addToBackStack(null)
            .commit()
    }

    private fun getHighlightListPosts() {

        mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
            .whereEqualTo("user_verified", true)
            .orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { documentSnapshot ->

                mPostList.clear()

                if(documentSnapshot != null) {
                    for(document in documentSnapshot.documents) {
                        val post: Post? = document.toObject(Post::class.java)
                        if(post != null) {
                            mPostList.add(post)
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
            }.addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }

    private fun getPostLikes() {

        mUser.post_likes.clear()

        mDatabase.collection(MyFirebase.COLLECTIONS.POST_LIKES)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                for(document in querySnapshot.documents) {
                    val postLike = document.toObject(PostLike::class.java)
                    if(postLike != null) {
                        mUser.post_likes.add(postLike)
                    }
                }
                mRecyclerView.isGone = false
                setRecyclerView()
                getHighlightListPosts()
            }
    }

    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }
}
