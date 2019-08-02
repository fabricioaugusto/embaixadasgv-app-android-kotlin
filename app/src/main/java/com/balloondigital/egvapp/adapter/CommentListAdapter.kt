package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.BasicUser
import com.balloondigital.egvapp.model.PostComment
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class CommentListAdapter(commentList: List<PostComment>): RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    private val mCommentList: List<PostComment> = commentList
    var onItemClick: ((comment: PostComment) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.adapter_post_comment, parent, false)

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

        init {

        }

        fun bindData(comment: PostComment) {

            val user: BasicUser = comment.user
            mTxtAdCommentUserName.text = user.name
            mTxtAdComment.text = comment.text

            Glide.with(context)
                .load(user.profile_img)
                .into(mImgAdCommentUser)

        }
    }
}