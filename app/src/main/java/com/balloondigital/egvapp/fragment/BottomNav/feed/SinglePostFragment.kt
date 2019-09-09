package com.balloondigital.egvapp.fragment.BottomNav.feed


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.CommentListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.PostComment
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
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
    private lateinit var mTag: String
    private lateinit var mFragName: String
    private lateinit var mNavBar: BottomNavigationView
    private lateinit var mPostCommentList: MutableList<PostComment>
    private lateinit var mAdapter: CommentListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPostID: String
    private lateinit var mPost: Post
    private lateinit var mUser: User
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


        mPostCommentList = mutableListOf()
        val bundle: Bundle? = arguments
        if (bundle != null) {
            mFragName = bundle.getString("frag_name").toString()
            mTag = bundle.getString("frag_tag").toString()
            mPostID = bundle.getString("post_id").toString()
            mUser = bundle.getSerializable("user") as User
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
    }

    private fun setListeners() {
        btPostSendComment.setOnClickListener(this)
        btPostOptions.setOnClickListener(this)
        btBackPress.setOnClickListener(this)

        etCommentMessage.onFocusChangeListener = View.OnFocusChangeListener {
                view, bool ->
            if(view.id == R.id.etCommentMessage) {
                if(bool) {
                   mRecyclerView.scrollToPosition(mPostCommentList.size-1)
                }
            }
        }
    }

    private fun getPost() {

        mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
            .document(mPostID)
            .addSnapshotListener {
                snapshot, e ->

                makeToast("Post Foi para o listener")

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
                            if(mPost.post_comments != post.post_comments) {
                                getComments()
                            }
                            mPost = post
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
            .orderBy("date", Query.Direction.DESCENDING)
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
                        txtPostEmptyComments.isGone = false
                    }
                    mAdapter.notifyDataSetChanged()
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


    private fun removeComment(comment: PostComment) {

        val alertbox = AlertDialog.Builder(mContext)

        if(comment.user_id == mCurrentUser!!.uid)   {
            alertbox.setItems(R.array.comments_author_alert, DialogInterface.OnClickListener { dialog, pos ->
                if(pos == 0) {
                    val deleteComment = mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS)
                        .document(comment.id).delete()
                    confirmRemoveCommentDialog("Deletar Comentário",
                        "Tem certeza que deseja remover esta publicação?", comment, deleteComment)
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
            }
            "post" -> {
                txtPostTitle.isGone = true
            }
            "thoght" -> {
                txtPostTitle.isGone = true
                imgPostUser.isGone = true
            }
        }

        val postDate = Converters.dateToString(mPost.date!!)
        txtPostDate.text = "${postDate.date} ${postDate.monthAbr} ${postDate.fullyear} às ${postDate.hours}:${postDate.minutes}"

        val user: User = mPost.user

        Glide.with(this)
            .load(user.profile_img!!.toUri())
            .transition(DrawableTransitionOptions.withCrossFade())
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

                txtPostEmptyComments.isGone = true

                document.update("id", document.id)
                postComment.id = document.id
                mPost.post_comments += 1
                mPostCommentList.add(postComment)
                mRecyclerView.scrollToPosition(0)

                mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
                    .document(mPostID).update("post_comments", mPost.post_comments)
            }
    }


    private fun updatePostInList() {

        if(mFragName == "HighlightPostsFragment") {
            val manager = activity!!.supportFragmentManager
            val fragment: Fragment? = manager.findFragmentByTag(mTag)
            val rootListPost: HighlightPostsFragment = fragment as HighlightPostsFragment
            rootListPost.updatePost(mPost)
        }

        if(mFragName == "EmbassyPostsFragment") {
            val manager = activity!!.supportFragmentManager
            val fragment: Fragment? = manager.findFragmentByTag(mTag)
            val rootListPost: EmbassyPostsFragment = fragment as EmbassyPostsFragment
            rootListPost.updatePost(mPost)
        }

        if(mFragName == "AllPostsFragment") {
            val manager = activity!!.supportFragmentManager
            val fragment: Fragment? = manager.findFragmentByTag(mTag)
            val rootListPost: AllPostsFragment = fragment as AllPostsFragment
            rootListPost.updatePost(mPost)
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

    private fun confirmRemoveCommentDialog(dialogTitle: String, dialogMessage: String, comment: PostComment, task: Task<Void>) {
        AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Sim") { dialog, which ->
                task.addOnCompleteListener {
                    mPostCommentList.remove(comment)
                    mAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }

}
