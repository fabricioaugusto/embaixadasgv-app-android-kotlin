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
import com.balloondigital.egvapp.model.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import de.hdodenhof.circleimageview.CircleImageView
import io.github.mthli.knife.KnifeParser
import android.os.AsyncTask
import android.util.Log
import androidx.core.net.toUri
import androidx.core.view.isGone
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.PostLike
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.like.LikeButton
import com.like.OnLikeListener


class PostListAdapter(postList: List<Post>): RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        mDatabase = MyFirebase.database()
        mPostCollection = mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
        mLikesCollection = mDatabase.collection(MyFirebase.COLLECTIONS.POST_LIKES)
        mContext = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        var view: View? = null


        view = when (viewType) {
            THOUGHT -> inflater.inflate(R.layout.adapter_post_thoughts, parent, false)
            NOTE -> inflater.inflate(R.layout.adapter_post_article, parent, false)
            else -> inflater.inflate(R.layout.adapter_post_picture, parent, false)
        }

        return PostViewHolder(view, mContext)
    }

    override fun getItemViewType(position: Int): Int {
        val post: Post = mPostList[position]

        mType = post.type

        return when (post.type) {
            "thought" -> THOUGHT
            "note" -> NOTE
            else -> PICTURE
        }
    }

    override fun getItemId(position: Int): Long {
        return mPostList[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return  mPostList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: Post = mPostList[position]
        holder.bindData(post)
    }



    inner class PostViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        private val mImgAdPostUser: CircleImageView = itemView.findViewById(R.id.imgAdPostUser)
        private val mTxtAdPostUserName: TextView = itemView.findViewById(R.id.txtAdPostUserName)
        private val mTxtAdPostDate: TextView = itemView.findViewById(R.id.txtAdPostDate)
        private val mTxtAdPostText: TextView = itemView.findViewById(R.id.txtAdPostText)
        private val mButtomLike: LikeButton = itemView.findViewById(R.id.btLikeButton)
        private val mTxtAdPostLikes: TextView = itemView.findViewById(R.id.txtAdPostLikes)
        private var mImgAdPostPicture: ImageView = itemView.findViewById(R.id.imgAdPostPicture)
        private var mTxtAdPostTitle: TextView = itemView.findViewById(R.id.txtAdPostTitle)

        init {

        }

        fun bindData(post: Post) {

            val user: BasicUser = post.user

            Log.d("AdapterPost", user.profile_img.toString())

            Glide.with(context)
                .load(user.profile_img!!.toUri())
                .transition(withCrossFade())
                .into(mImgAdPostUser)

            mTxtAdPostUserName.text = user.name
            mTxtAdPostText.text = KnifeParser.fromHtml(post.text)

            if(!post.title.isNullOrEmpty()) {
                mTxtAdPostTitle.text = post.title
            }

            if(!post.picture.isNullOrEmpty()) {
                Glide.with(context)
                    .load(post.picture!!.toUri())
                    .transition(withCrossFade())
                    .into(mImgAdPostPicture)
            }

            val likeListener = object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {

                    val postLike: PostLike = PostLike(post_id = post.id, user_id = user.id, user = user)
                    mLikesCollection.add(postLike).addOnSuccessListener {
                        it.update("id", it.id)
                    }
                }

                override fun unLiked(likeButton: LikeButton?) {

                }

            }

            mButtomLike.setOnLikeListener(likeListener)

            /*if(post.post_likes > 0) {

                mTxtAdPostLikes.isGone = false

                mDatabase.collection(MyFirebase.COLLECTIONS.POST_LIKES)
                    .whereEqualTo("post_id", post.id)
                    .whereEqualTo("user_id", user.id)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document != null) {
                            mButtomLike.isLiked
                        }
                    }


                if(mButtomLike.isLiked) {
                    mButtomLike.setOnClickListener(View.OnClickListener {
                        postDocument.update("post_likes", post.post_likes-1)

                        val postLike: PostLike = PostLike(post_id = post.id, user_id = user.id, user = user)
                        mLikesCollection.add(postLike.toMap())

                    })
                } else {
                    mButtomLike.setOnClickListener(View.OnClickListener {
                        postDocument.update("post_likes", post.post_likes+1)
                    })
                }
            } else {
                mTxtAdPostLikes.isGone = false
            }*/

        }
    }
}