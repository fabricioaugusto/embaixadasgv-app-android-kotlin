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
import com.balloondigital.egvapp.model.PostLike
import com.balloondigital.egvapp.model.User
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HighlightsFragment : Fragment(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: PostListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mPostList: MutableList<Post>
    private lateinit var mListLikes: MutableList<PostLike>
    private var mAdapterPosition: Int = 0
    private lateinit var mUser: User
    private lateinit var mDbListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_highlights, container, false)

        mContext = view.context
        mAuth = MyFirebase.auth()
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutFeed)
        mDatabase = MyFirebase.database()
        mPostList = mutableListOf()
        mRecyclerView = view.findViewById(R.id.postsRecyclerView)

        setListeners()
        getAuthUserDetails()

        return view
    }

    override fun onClick(view: View) {
        val id = view.id

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
        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getListPosts()
        })
    }

    private fun setRecyclerView() {

        mAdapter = PostListAdapter(mPostList, mUser, activity!!)
        mAdapter.setHasStableIds(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_post)
            .shimmer(true).show()

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

    private fun getAuthUserDetails() {

        val currentUser = mAuth.currentUser

        if(currentUser != null) {
            mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener {
                    documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null) {
                        mUser = user
                        getPostLikes()
                    }
                }
        }


    }

    private fun getListPosts() {

        mDatabase.collection(MyFirebase.COLLECTIONS.POSTS).whereEqualTo("user_verified", true)
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
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
            } .addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }

    private fun getPostLikes() {
        mDatabase.collection(MyFirebase.COLLECTIONS.POST_LIKES)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                for(document in querySnapshot) {
                    val postLike = document.toObject(PostLike::class.java)
                    mListLikes.add(postLike)
                }
                setRecyclerView()
                getListPosts()
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
