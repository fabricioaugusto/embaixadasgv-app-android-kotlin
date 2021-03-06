package com.balloondigital.egvapp.fragment.feed


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.CommentListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.fragment.search.SingleUserFragment
import com.balloondigital.egvapp.model.Notification
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.PostComment
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.fragment_single_post.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SinglePostFragment : Fragment(), View.OnClickListener {


    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mToolbar: Toolbar
    private lateinit var mNavBar: BottomNavigationView
    private lateinit var mPostCommentList: MutableList<PostComment>
    private lateinit var mAdapter: CommentListAdapter
    private lateinit var mRegistration: ListenerRegistration
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTxtPostEmptyComments: TextView
    private lateinit var mPostID: String
    private lateinit var mPost: Post
    private lateinit var mUser: User
    private var mRootView: Int = 0
    private var isCommented: Boolean = false
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_single_post, container, false)

        mNavBar = activity!!.findViewById(R.id.navView)
        mNavBar.isGone = true

        mToolbar = view.findViewById(R.id.singlePostToolbar)
        mToolbar.title = ""

        mContext = view.context
        mDatabase = MyFirebase.database()
        mCurrentUser = UserService.authCurrentUser()
        mTxtPostEmptyComments = view.findViewById(R.id.txtPostEmptyComments)

        mPostCommentList = mutableListOf()
        val bundle: Bundle? = arguments
        if (bundle != null) {
            mPostID = bundle.getString("post_id").toString()
            mUser = bundle.getSerializable("user") as User
            mRootView = bundle.getInt("rootViewer")
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = recyclerViewComments
        setListeners()
        getPost()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btPostSendComment) {
            saveData()
        }

        if(id == R.id.btPostOptions) {
            removePost()
        }

        if(id == R.id.btBackPress) {
            mNavBar.isGone = false
            activity!!.onBackPressed()
        }

        if(id == R.id.txtLikeCount) {
            startLikesUsersList()
        }

        if(id == R.id.layoutUserProfile) {
            startSingleUserActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mNavBar.isGone = false

        if (::mRegistration.isInitialized) {
            mRegistration.remove()
        }
    }

    private fun setListeners() {
        layoutUserProfile.setOnClickListener(this)
        txtLikeCount.setOnClickListener(this)
        btPostSendComment.setOnClickListener(this)
        btPostOptions.setOnClickListener(this)
        btBackPress.setOnClickListener(this)

        etCommentMessage.onFocusChangeListener = View.OnFocusChangeListener {
                view, bool ->
            if(view.id == R.id.etCommentMessage) {
                if(bool) {
                    Handler().postDelayed({
                        scrollToBottom()
                    }, 500)
                }
            }
        }
    }

    private fun getPost() {

        mRegistration = mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
            .document(mPostID)
            .addSnapshotListener {
                snapshot, e ->

                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val post: Post? = snapshot.toObject(Post::class.java)
                    if(post != null) {

                        if(!::mPost.isInitialized) {
                            mPost = post
                            setRecyclerView()
                            bindData()
                            getComments()
                        } else {
                            val oldPost = mPost
                            mPost = post
                            if(oldPost.post_comments != post.post_comments) {
                                getComments()
                            }
                            updatePostInList()
                        }
                    }
                } else {

                }
            }
    }

    private fun getComments() {

        mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS)
            .whereEqualTo("post_id", mPost.id)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->

                if (querySnapshot != null) {
                    mPostCommentList.clear()
                    if(querySnapshot.size() > 0) {
                        for(item in querySnapshot) {
                            val comment = item.toObject(PostComment::class.java)
                            mPostCommentList.add(comment)
                        }
                    } else {
                        mTxtPostEmptyComments.isGone = false
                    }
                    mAdapter.notifyDataSetChanged()
                    if(isCommented) {
                        Handler().postDelayed({
                            scrollToBottom()
                        }, 300)
                        isCommented = false
                    }
                }
            }.addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }

    private fun removePost() {

        val alertbox = AlertDialog.Builder(mContext)

        if(mPost.user.id == mCurrentUser!!.uid)   {
            alertbox.setItems(R.array.posts_author_alert, DialogInterface.OnClickListener { dialog, pos ->
                if(pos == 0) {
                    val deletePost = mDatabase.collection(MyFirebase.COLLECTIONS.POSTS).document(mPost.id).delete()
                    confirmDialog("Deletar Publicação",
                        "Tem certeza que deseja remover esta publicação?", deletePost)
                }
            })
        } else {
            alertbox.setItems(R.array.posts_alert, DialogInterface.OnClickListener { dialog, pos ->
                //pos will give the selected item position
            })
        }
        alertbox.show()
    }


    private fun setRecyclerView() {

        mAdapter = CommentListAdapter(mPostCommentList, mPost)
        mAdapter.setHasStableIds(true)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mAdapter.onItemClick = {
                comment ->

        }
    }

    private fun bindData() {

        when(mPost.type) {
            "note" -> {
                txtPostTitle.text = mPost.title
                txtPostText.movementMethod = LinkMovementMethod.getInstance()
            }
            "post" -> {
                txtPostTitle.isGone = true
            }
            "thought" -> {
                txtPostTitle.isGone = true
                imgPostPicture.isGone = true
                txtPostText.textSize = 24F
            }
        }

        val postDate = Converters.dateToString(mPost.date!!)
        txtPostDate.text = "${postDate.date} ${postDate.monthAbr} ${postDate.fullyear} às ${postDate.hours}:${postDate.minutes}"

        if(mPost.post_likes > 0) {
            txtLikeCount.isGone = false
            if(mPost.post_likes == 1) {
                txtLikeCount.text = "1 curtida"
            } else {
                txtLikeCount.text = "${mPost.post_likes} curtidas"
            }
        } else {
            txtLikeCount.isGone = true
        }

        val user: User = mPost.user

        val requestOptions: RequestOptions = RequestOptions()
        val options = requestOptions.transforms(CenterCrop(), RoundedCorners(60))

        Glide.with(this)
            .load(user.profile_img!!.toUri())
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(options)
            .into(imgPostUser)

        txtPostUserName.text = user.name
        txtPostText.text = KnifeParser.fromHtml(mPost.text)

        if(!mPost.picture.isNullOrEmpty()) {
            Glide.with(this)
                .load(mPost.picture!!.toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgPostPicture)
        }
    }

    private fun saveData() {

        val comment = etCommentMessage.text.toString()

        if(comment.isNullOrEmpty()){
            return
        }

        etCommentMessage.setText("")

        val postComment: PostComment = PostComment(
            post_id = mPostID,
            user_id = mCurrentUser!!.uid,
            text = comment,
            user = mUser)

        mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS).add(postComment.toMap())
            .addOnSuccessListener {
                    document ->

                isCommented = true

                mTxtPostEmptyComments.isGone = true

                document.update("id", document.id)
                postComment.id = document.id
                val post_comments = mPost.post_comments + 1
                mPostCommentList.add(postComment)

                mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
                    .document(mPostID).update("post_comments", post_comments)

                setNotification("post_comment", postComment.id)
            }
    }


    private fun setNotification(type: String, commentID: String) {

        val notification = Notification()
        notification.type = type
        notification.post_id = mPostID
        notification.title = "<b>${mUser.name}</b> comentou em sua publicação"
        notification.picture = mUser.profile_img.toString()
        notification.receiver_id = mPost.user_id
        notification.comment_id = commentID

        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .add(notification.toMap())
    }

    private fun startLikesUsersList() {

        val bundle = Bundle()
        bundle.putString("type", "post_likes")
        bundle.putString("obj_id", mPostID)
        bundle.putInt("rootViewer", mRootView)

        val nextFrag = UsersListFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootView, nextFrag, "$mRootView:likeUsers")
            .addToBackStack(null)
            .commit()
    }

    private fun startSingleUserActivity() {
        val bundle = Bundle()
        bundle.putSerializable("user", mPost.user)

        val nextFrag = SingleUserFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootView, nextFrag, "$mRootView:singleUser")
            .addToBackStack(null)
            .commit()
    }

    private fun updatePostInList() {


        val fragTagPrefix = "android:switcher:${R.id.viewpager}"

        val manager = activity!!.supportFragmentManager
        val highLightPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:0")
        val embassyPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:1")
        val allPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:2")

        if(highLightPostsfragment != null && highLightPostsfragment.isVisible) {
            val rootHighlightListPosts: HighlightPostsFragment = highLightPostsfragment as HighlightPostsFragment
            rootHighlightListPosts.updatePost(mPost)
        }

        if(embassyPostsfragment != null && embassyPostsfragment.isVisible) {
            val rootEmbassyListPost: EmbassyPostsFragment = embassyPostsfragment as EmbassyPostsFragment
            rootEmbassyListPost.updatePost(mPost)
        }

        if(allPostsfragment != null && allPostsfragment.isVisible) {
            val rootAllListPost: AllPostsFragment = allPostsfragment as AllPostsFragment
            rootAllListPost.updatePost(mPost)
        }

    }

    private fun updateListPost() {

        val manager = activity!!.supportFragmentManager
        val fragment: Fragment? = manager.findFragmentByTag("rootFeedFragment")
        val rootListPost: ListPostFragment = fragment as ListPostFragment
        rootListPost.updateListPost()

    }

    private fun confirmDialog(dialogTitle: String, dialogMessage: String, task: Task<Void>) {
        AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Sim") { dialog, which ->
                task.addOnCompleteListener {
                    updateListPost()
                    activity!!.onBackPressed()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun scrollToBottom() {
        val lastChild: View = nsvSinglePost.getChildAt(nsvSinglePost.childCount - 1)
        val bottom = lastChild.bottom + nsvSinglePost.paddingBottom
        val sy = nsvSinglePost.scrollY
        val sh = nsvSinglePost.height
        val delta = bottom - (sy + sh)
        nsvSinglePost.smoothScrollBy(0, delta)
    }

    fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }

}
