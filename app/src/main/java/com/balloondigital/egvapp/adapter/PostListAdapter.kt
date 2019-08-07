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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import de.hdodenhof.circleimageview.CircleImageView
import io.github.mthli.knife.KnifeParser
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.ImageButton
import androidx.core.net.toUri
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.model.PostLike
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.like.LikeButton
import com.like.OnLikeListener
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task


class PostListAdapter(postList: MutableList<Post>): RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private val mPostList: MutableList<Post> = postList
    private lateinit var mContext: Context
    var onItemClick: ((post: Post, position: Int) -> Unit)? = null
    private val THOUGHT: Int = 0
    private val NOTE: Int = 1
    private val PICTURE: Int = 2
    private lateinit var mType: String
    private lateinit var mUserID: String
    private var mLikeID: String? = null
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mPostCollection: CollectionReference
    private lateinit var mLikesCollection: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        mDatabase = MyFirebase.database()
        mPostCollection = mDatabase.collection(MyFirebase.COLLECTIONS.POSTS)
        mLikesCollection = mDatabase.collection(MyFirebase.COLLECTIONS.POST_LIKES)
        mContext = parent.context
        mUserID = UserService.authCurrentUser()!!.uid
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
        holder.bindData(post, position)
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
        private var mBtAdPostOptions: ImageButton = itemView.findViewById(R.id.btAdPostOptions)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mPostList[adapterPosition], adapterPosition)
            }
        }

        fun bindData(post: Post, position: Int) {

            val user: User = post.user
            mButtomLike.isLiked = post.liked


            if(!post.like_verified) {
                mLikesCollection.whereEqualTo("post_id", post.id)
                    .whereEqualTo("user_id", mUserID)
                    .get().addOnSuccessListener {
                            data ->
                        if(data.documents.size > 0) {
                            Log.d("PostAdapterLog", "Achou")
                            mLikeID = data.documents[0].id
                            mButtomLike.isLiked = true
                            post.liked = true
                        }
                        post.like_verified = true
                    }
            }

            if(post.post_likes > 0) {
                mTxtAdPostLikes.text = post.post_likes.toString()
            }

            val likeListener = object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    post.liked = true
                    mTxtAdPostLikes.text = (post.post_likes+1).toString()

                    val postLike: PostLike = PostLike(post_id = post.id, user_id = mUserID, user = user)
                    post.post_likes = post.post_likes+1
                    mLikesCollection.add(postLike).addOnSuccessListener {
                        it.update("id", it.id)
                        mPostCollection.document(post.id).update("post_likes", post.post_likes)
                    }
                }

                override fun unLiked(likeButton: LikeButton?) {
                    post.liked = false

                    val numLikes = post.post_likes-1

                    if(numLikes > 0) {
                        mTxtAdPostLikes.text = (post.post_likes-1).toString()
                    } else {
                        mTxtAdPostLikes.text = ""
                    }

                    post.post_likes = numLikes
                    mPostCollection.document(post.id).update("post_likes", numLikes)

                    if(mLikeID != null) {
                        mLikesCollection.document(mLikeID!!).delete()
                    }
                }
            }

            mButtomLike.setOnLikeListener(likeListener)

            Glide.with(itemView.context)
                .load(user.profile_img!!.toUri())
                .transition(withCrossFade())
                .into(mImgAdPostUser)

            mTxtAdPostUserName.text = user.name
            mTxtAdPostText.text = KnifeParser.fromHtml(post.text)

            if(post.type == "thought") {
                mTxtAdPostText.movementMethod = LinkMovementMethod.getInstance()
            }


            if(!post.title.isNullOrEmpty()) {
                mTxtAdPostTitle.text = post.title
            }

            if(!post.picture.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(post.picture!!.toUri())
                    .transition(withCrossFade())
                    .into(mImgAdPostPicture)
            }


            mBtAdPostOptions.setOnClickListener(View.OnClickListener {
                val alertbox = AlertDialog.Builder(context)


                if(user.id == mUserID)   {
                    alertbox.setItems(R.array.posts_author_alert, DialogInterface.OnClickListener { dialog, pos ->
                        if(pos == 0) {
                            val deletePost = mPostCollection.document(post.id).delete()
                            confirmDialog("Deletar Publicação",
                                "Tem certeza que deseja remover esta publicação?", deletePost, position)
                        }
                    })
                } else {
                    alertbox.setItems(R.array.posts_alert, DialogInterface.OnClickListener { dialog, pos ->
                        //pos will give the selected item position
                    })
                }
                alertbox.show()
            })
        }

        private fun confirmDialog(dialogTitle: String, dialogMessage: String, task: Task<Void>, position: Int) {
            AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Sim") { dialog, which ->
                    task.addOnCompleteListener {
                        mPostList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, mPostList.size)
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}