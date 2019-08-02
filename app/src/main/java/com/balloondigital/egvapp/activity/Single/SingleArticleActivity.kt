package com.balloondigital.egvapp.activity.Single

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.CommentListAdapter
import com.balloondigital.egvapp.adapter.PostListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.model.BasicUser
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.PostComment
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_single_article.*

class SingleArticleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mPostCommentList: MutableList<PostComment>
    private lateinit var mAdapter: CommentListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPostID: String
    private lateinit var mPost: Post
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_article)

        //init vars
        mDatabase = MyFirebase.database()
        mCurrentUser = UserService.authCurrentUser()
        mRecyclerView = recyclerViewComments
        mPostCommentList = mutableListOf()
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mPostID = bundle.getString("post_id").toString()
        }

        setListeners()
        getPost()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btPostSendComment) {
            saveData()
        }
    }

    private fun setListeners() {
        btPostSendComment.setOnClickListener(this)
    }

    private fun getPost() {
        mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
            .document(mPostID)
            .get().addOnSuccessListener {
                documentSnapshot ->

                val post: Post? = documentSnapshot.toObject(Post::class.java)
                if(post != null) {
                    mPost = post
                    bindData()
                    getComments()
                }
            }
    }

    private fun getComments() {
        mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS)
            .addSnapshotListener { listSnapshot, e ->
                if (e != null) {
                    Log.w("", "Listen failed.", e)
                }

                if(listSnapshot != null) {
                    for(item in listSnapshot) {
                        Log.d("FirebaseLog", "Achou comentários")
                        val comment = item.toObject(PostComment::class.java)
                        val userRef = comment.user_ref
                        userRef?.get()?.addOnSuccessListener { document ->
                            val user = document.toObject(User::class.java)
                            if(user != null) {
                                val basicUser = BasicUser(id=user.id, name=user.name, email=user.email, profile_img = user.profile_img)
                                comment.user = basicUser
                                mPostCommentList.add(comment)
                            }
                        }
                    }
                    setRecyclerView()
                }
            }
    }

    private fun setRecyclerView() {

        mAdapter = CommentListAdapter(mPostCommentList)
        mAdapter.setHasStableIds(true)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter.onItemClick = {
                comment ->

        }
    }

    private fun bindData() {

        val user: BasicUser = mPost.user

        Glide.with(this)
            .load(user.profile_img!!.toUri())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imgPostUser)

        txtPostUserName.text = user.name
        txtPostText.text = KnifeParser.fromHtml(mPost.text)


        if(!mPost.title.isNullOrEmpty()) {
            txtPostTitle.text = mPost.title
        }

        if(!mPost.picture.isNullOrEmpty()) {
            Glide.with(this)
                .load(mPost.picture!!.toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgPostPicture)
        }
    }

    private fun saveData() {

        val comment = etCommentMessage.text.toString()
        etCommentMessage.setText("")

        if(comment.isNullOrEmpty()){
            return
        }

        val userRef = mDatabase.collection(MyFirebase.COLLECTIONS.USERS).document(mCurrentUser!!.uid)
        val postComment: PostComment = PostComment(
            post_id = mPostID,
            user_id = mCurrentUser!!.uid,
            text = comment,
            user_ref = userRef)

        mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS).add(postComment.toMap())
            .addOnSuccessListener {
                document ->
                document.update("id", document.id)
                makeToast("Comentário adicionado com sucesso!")
            }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
