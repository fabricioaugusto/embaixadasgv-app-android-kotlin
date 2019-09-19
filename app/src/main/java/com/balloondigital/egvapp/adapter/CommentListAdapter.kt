package com.balloondigital.egvapp.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.DateStr
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.PostComment
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class CommentListAdapter(commentList: MutableList<PostComment>, post: Post): RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    private lateinit var mAuth: FirebaseAuth
    private val mPost: Post = post
    private lateinit var mCurrentUser: FirebaseUser
    private lateinit var mDatabase: FirebaseFirestore
    private val mCommentList: MutableList<PostComment> = commentList
    var onItemClick: ((comment: PostComment) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.adapter_post_comment, parent, false)

        mAuth = MyFirebase.auth()
        val currentUser: FirebaseUser? = mAuth.currentUser
        if(currentUser != null) mCurrentUser = currentUser
        mDatabase = MyFirebase.database()



        return CommentViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mCommentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment: PostComment = mCommentList[position]
        holder.bindData(comment)
    }

    inner class CommentViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mTxtAdCommentUserName: TextView = itemView.findViewById(R.id.txtAdCommentUserName)
        val mImgAdCommentUser: CircleImageView = itemView.findViewById(R.id.imgAdCommentUser)
        val mTxtAdComment: TextView = itemView.findViewById(R.id.txtAdComment)
        val mTxtAdCommentDate: TextView = itemView.findViewById(R.id.txtAdCommentDate)
        val mBtAdCommentOptions: ImageButton = itemView.findViewById(R.id.btAdCommentOptions)

        init {

        }

        fun bindData(comment: PostComment) {


            var commentDate: DateStr = Converters.dateToString(Timestamp.now())

            if(comment.date != null) {
                commentDate = Converters.dateToString(comment.date!!)
            }

            mTxtAdCommentDate.text = "${commentDate.date}/${commentDate.month}/${commentDate.fullyear} às ${commentDate.hours}:${commentDate.minutes}"

            val user: User = comment.user
            mTxtAdCommentUserName.text = user.name
            mTxtAdComment.text = comment.text

            Glide.with(context)
                .load(user.profile_img)
                .into(mImgAdCommentUser)


            mBtAdCommentOptions.isGone =
                !(comment.user_id == mCurrentUser.uid || comment.user_id == mPost.user_id)

            mBtAdCommentOptions.setOnClickListener {
                val alertbox = AlertDialog.Builder(context)

                if(comment.user_id == mCurrentUser.uid)   {
                    alertbox.setItems(R.array.comments_author_alert, DialogInterface.OnClickListener { dialog, pos ->
                        if(pos == 0) {
                            confirmRemoveCommentDialog("Deletar Comentário",
                                "Tem certeza que deseja remover este comentário?", comment)
                        }
                    })
                } else {
                    alertbox.setItems(R.array.posts_alert, DialogInterface.OnClickListener { dialog, pos ->
                        //pos will give the selected item position
                    })
                }
                alertbox.show()
            }
        }

        private fun confirmRemoveCommentDialog(dialogTitle: String, dialogMessage: String, comment: PostComment) {
            AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Sim") { dialog, which ->
                    mDatabase.collection(MyFirebase.COLLECTIONS.POST_COMMENTS)
                        .document(comment.id).delete()
                        .addOnCompleteListener {
                            mCommentList.remove(comment)
                            mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
                                .document(mPost.id).update("post_comments", mCommentList.size)
                    }
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}