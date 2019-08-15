package com.balloondigital.egvapp.activity.Single

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.CommentListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.PostComment
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_single_post.*
import kotlinx.android.synthetic.main.activity_single_thought.*
import kotlinx.android.synthetic.main.activity_single_thought.btPostOptions
import kotlinx.android.synthetic.main.activity_single_thought.btPostSendComment
import kotlinx.android.synthetic.main.activity_single_thought.etCommentMessage
import kotlinx.android.synthetic.main.activity_single_thought.imgPostUser
import kotlinx.android.synthetic.main.activity_single_thought.recyclerViewComments
import kotlinx.android.synthetic.main.activity_single_thought.txtPostEmptyComments
import kotlinx.android.synthetic.main.activity_single_thought.txtPostText
import kotlinx.android.synthetic.main.activity_single_thought.txtPostUserName

class SingleThoughtActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mPostCommentList: MutableList<PostComment>
    private lateinit var mAdapter: CommentListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPostID: String
    private lateinit var mPost: Post
    private lateinit var mUser: User
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_thought)

        supportActionBar!!.title = "Publicação"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = MyFirebase.database()
        mCurrentUser = UserService.authCurrentUser()

        mRecyclerView = recyclerViewComments
        mPostCommentList = mutableListOf()
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mPostID = bundle.getString("post_id").toString()
            mUser = bundle.getSerializable("user") as User
        }

        setListeners()
        getPost()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> true
        }
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btPostSendComment) {
            saveData()
        }

        if(id == R.id.btPostOptions) {
            removePost()
        }
    }

    private fun setListeners() {
        btPostSendComment.setOnClickListener(this)
        btPostOptions.setOnClickListener(this)
    }

    private fun getPost() {
        mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
            .document(mPostID)
            .get().addOnSuccessListener {
                    documentSnapshot ->

                val post: Post? = documentSnapshot.toObject(Post::class.java)
                if(post != null) {
                    mPost = post
                    setRecyclerView()
                    bindData()
                    getComments()
                }
            }
    }

    private fun getComments() {
        mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS)
            .whereEqualTo("post_id", mPost.id)
            .orderBy("date", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { listSnapshot ->
                if(listSnapshot != null) {
                    if(listSnapshot.size() > 0) {
                        mPostCommentList.clear()
                        for(item in listSnapshot) {
                            val comment = item.toObject(PostComment::class.java)
                            mPostCommentList.add(comment)
                        }
                    } else {
                        txtPostEmptyComments.isGone = false
                    }
                }
                mAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("EGVTAG", it.message.toString())
            }
    }

    private fun removePost() {

        val alertbox = AlertDialog.Builder(this)

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

        val alertbox = AlertDialog.Builder(this)

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
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter.onItemClick = {
                comment ->

        }
    }

    private fun bindData() {

        val user: User = mPost.user

        Glide.with(this)
            .load(user.profile_img!!.toUri())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imgPostUser)

        txtPostUserName.text = user.name
        txtPostText.text = KnifeParser.fromHtml(mPost.text)
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

                document.update("id", document.id)
                postComment.id = document.id
                mPost.post_comments += 1
                mPostCommentList.add(postComment)

                mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
                    .document(mPostID).update("post_comments", mPost.post_comments)


                mAdapter.notifyDataSetChanged()
            }
    }

    private fun confirmDialog(dialogTitle: String, dialogMessage: String, task: Task<Void>) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Sim") { dialog, which ->
                task.addOnCompleteListener {
                    val returnIntent = Intent()
                    returnIntent.putExtra("removed", true)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun confirmRemoveCommentDialog(dialogTitle: String, dialogMessage: String, comment: PostComment, task: Task<Void>) {
        AlertDialog.Builder(this)
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
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
