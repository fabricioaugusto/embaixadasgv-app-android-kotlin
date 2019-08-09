package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.Post
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.like.LikeButton
import de.hdodenhof.circleimageview.CircleImageView
import io.github.mthli.knife.KnifeParser

class PostPictureListAdapter(postList: List<Post>): RecyclerView.Adapter<PostPictureListAdapter.PostPictureViewHolder>() {

    private val mPostList: List<Post> = postList
    private lateinit var mContext: Context
    var onItemClick: ((post: Post) -> Unit)? = null
    private val THOUGHT: Int = 0
    private val NOTE: Int = 1
    private val PICTURE: Int = 2
    private lateinit var mType: String
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mPostCollection: CollectionReference
    private lateinit var mLikesCollection: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostPictureViewHolder {

        mContext = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = inflater.inflate(R.layout.adapter_post_picture, parent, false)


        return PostPictureViewHolder(view, mContext)
    }


    override fun getItemId(position: Int): Long {
        return mPostList[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return  mPostList.size
    }

    override fun onBindViewHolder(holder: PostPictureViewHolder, position: Int) {
        val post: Post = mPostList[position]
        holder.bindData(post)
    }


    inner class PostPictureViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        private val mImgAdPostUser: CircleImageView = itemView.findViewById(R.id.imgAdPostUser)
        private val mTxtAdPostUserName: TextView = itemView.findViewById(R.id.txtAdPostUserName)
        private val mTxtAdPostDate: TextView = itemView.findViewById(R.id.txtAdPostDate)
        private val mTxtAdPostText: TextView = itemView.findViewById(R.id.txtAdPostText)
        private val mImgAdPostPicture: ImageView  = itemView.findViewById(R.id.imgAdPostPicture)
        private val mButtomLike: LikeButton = itemView.findViewById(R.id.btLikeButton)

        init {

        }

        fun bindData(post: Post) {

            val user: User = post.user

            Glide.with(context)
                .load(user.profile_img)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mImgAdPostUser)

            mTxtAdPostUserName.text = user.name

            Glide.with(context)
                .load(post.picture)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mImgAdPostPicture)

            mTxtAdPostText.text = post.text

        }
    }
}