package com.balloondigital.egvapp.fragment.BottomNav.feed


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Create.CreateArticleActivity
import com.balloondigital.egvapp.activity.Create.CreatePostActivity
import com.balloondigital.egvapp.activity.Create.CreateToughtActivity
import com.balloondigital.egvapp.activity.Single.SingleArticleActivity
import com.balloondigital.egvapp.activity.Single.SinglePostActivity
import com.balloondigital.egvapp.activity.Single.SingleThoughtActivity
import com.balloondigital.egvapp.adapter.CreatePostDialogAdapter
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
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HighlightPostsFragment : Fragment(), OnItemClickListener {


    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: PostListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var mAdapterDialog: CreatePostDialogAdapter
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mPostList: MutableList<Post>
    private var mAdapterPosition: Int = 0
    private val CREATE_POST_ACTIVITY_CODE = 200
    private lateinit var mUser: User

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

        mAdapterDialog = CreatePostDialogAdapter(mContext, false, 3)
        mDatabase = MyFirebase.database()
        mPostList = mutableListOf()
        mRecyclerView = view.findViewById(R.id.postsRecyclerView)

        getPostLikes()
        setListeners()

        return view
    }

    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {
            val intent: Intent = Intent(mContext, CreateToughtActivity::class.java)
            intent.putExtra("user", mUser)
            startActivityForResult(intent, CREATE_POST_ACTIVITY_CODE)
        }

        if(position == 1) {
            val intent: Intent = Intent(mContext, CreateArticleActivity::class.java)
            intent.putExtra("user", mUser)
            startActivityForResult(intent, CREATE_POST_ACTIVITY_CODE)
        }

        if(position == 2) {
            val intent: Intent = Intent(mContext, CreatePostActivity::class.java)
            intent.putExtra("user", mUser)
            startActivityForResult(intent, CREATE_POST_ACTIVITY_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_feed_toolbar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.bar_create_post -> {
                setCreatePostDialog()
                return true
            }
            else -> true
        }
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
                CREATE_POST_ACTIVITY_CODE -> {
                    getPostLikes()
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

    private fun setListeners() {
        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getPostLikes() })
    }

    private fun setRecyclerView() {

        mAdapter = PostListAdapter(mPostList, mUser)
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

    private fun setCreatePostDialog() {

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(mContext)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapterDialog
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setHeader(R.layout.header_dialog)
            dialogBuilder.setPadding(16, 16, 16, 48)
            mCPDialog = dialogBuilder.create()
            mCPDialog.show()
        }
    }
}
