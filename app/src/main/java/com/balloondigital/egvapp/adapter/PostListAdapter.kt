package com.balloondigital.egvapp.adapter

import android.app.Activity
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
import android.content.Intent
import android.os.Handler
import android.text.Layout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toHtml
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.balloondigital.egvapp.activity.Single.UserProfileActivity
import com.balloondigital.egvapp.fragment.BottomNav.feed.AllPostsFragment
import com.balloondigital.egvapp.fragment.BottomNav.feed.EmbassyPostsFragment
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_submit_invite_code.*


class PostListAdapter(postList: MutableList<Post>, user: User, activity: FragmentActivity): RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private val mPostList: MutableList<Post> = postList
    private val mUser: User = user
    private val mActivity = activity
    private lateinit var mContext: Context
    var onItemClick: ((post: Post, position: Int) -> Unit)? = null
    private val THOUGHT: Int = 0
    private val NOTE: Int = 1
    private val PICTURE: Int = 2
    private lateinit var mType: String
    private var mLikeID: String? = null
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mPostCollection: CollectionReference
    private lateinit var mLikesCollection: CollectionReference
    private var mListLikes: MutableList<PostLike> = mUser.post_likes
    private var mLikeIsProcessing = false

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
        holder.bindData(post, position)
    }

    inner class PostViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        private val mImgAdPostUser: ImageView = itemView.findViewById(R.id.imgAdPostUser)
        private val mTxtAdPostUserName: TextView = itemView.findViewById(R.id.txtAdPostUserName)
        private val mTxtAdPostDate: TextView = itemView.findViewById(R.id.txtAdPostDate)
        private val mTxtAdPostText: TextView = itemView.findViewById(R.id.txtAdPostText)
        private val mButtomLike: LikeButton = itemView.findViewById(R.id.btLikeButton)
        private val mTxtAdPostLikes: TextView = itemView.findViewById(R.id.txtAdPostLikes)
        private var mImgAdPostPicture: ImageView = itemView.findViewById(R.id.imgAdPostPicture)
        private var mTxtAdPostTitle: TextView = itemView.findViewById(R.id.txtAdPostTitle)
        private var mBtAdPostOptions: ImageButton = itemView.findViewById(R.id.btAdPostOptions)
        private var mTxtAdPostComments: TextView = itemView.findViewById(R.id.txtAdPostComments)
        private var mImgProfileVerified: ImageView = itemView.findViewById(R.id.imgProfileVerified)
        private var mLayoutUserProfile: LinearLayout = itemView.findViewById(R.id.layoutUserProfile)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mPostList[adapterPosition], adapterPosition)
            }
        }

        fun bindData(post: Post, position: Int) {

            val user: User = post.user

            val requestOptions: RequestOptions = RequestOptions()
            val options = requestOptions.transforms(CenterCrop(), RoundedCorners(60))

            Glide.with(itemView.context)
                .load(user.profile_img!!.toUri())
                .transition(withCrossFade())
                .apply(options)
                .into(mImgAdPostUser)

            mButtomLike.isLiked = mListLikes.any { it.post_id == post.id }

            mImgProfileVerified.isGone = !post.user_verified

            if(post.post_likes > 0) {
                mTxtAdPostLikes.text = post.post_likes.toString()
            } else {
                mTxtAdPostLikes.text = ""
            }

            if(post.post_comments > 0) {
                mTxtAdPostComments.text = post.post_comments.toString()
            } else {
                mTxtAdPostComments.text = ""
            }

            val likeListener = object : OnLikeListener {
                override fun liked(likeButton: LikeButton) {

                    if(!mListLikes.any { it.post_id == post.id } && !mLikeIsProcessing){

                        val postLike: PostLike = PostLike(post_id = post.id, user_id = mUser.id, user = user)

                        mLikeIsProcessing = true

                        val numLikes = post.post_likes+1

                        mTxtAdPostLikes.text = numLikes.toString()

                        mLikesCollection.add(postLike.toMap()).addOnSuccessListener {
                            postLike.id = it.id
                            post.post_likes = numLikes
                            updatePosts(post, postLike, position, "like")
                            it.update("id", it.id).addOnSuccessListener {
                                mPostCollection.document(post.id).update("post_likes", post.post_likes)
                                mLikeIsProcessing = false
                            }
                        }
                            .addOnFailureListener {
                                Log.d("EGVAPPLOG", it.message.toString())
                            }
                    }
                }

                override fun unLiked(likeButton: LikeButton) {

                    if(mListLikes.any { it.post_id == post.id } && !mLikeIsProcessing){

                        val list = mListLikes.filter {it.post_id == post.id}

                        if(list.isNotEmpty()) {
                            mLikeIsProcessing = true

                            mListLikes.remove(list[0])
                            val numLikes = post.post_likes-1

                            mLikesCollection.document(list[0].id).delete()
                                .addOnSuccessListener {
                                    mPostCollection.document(post.id).update("post_likes", numLikes)
                                    post.post_likes = numLikes
                                    updatePosts(post, list[0], position, "unlike")
                                    mLikeIsProcessing = false
                                }
                                .addOnFailureListener {
                                    Log.d("EGVAPPLOGFAIL", it.message.toString())
                                }
                        }

                    }
                }
            }

            val postDate = Converters.dateToString(post.date!!)
            mTxtAdPostDate.text = "${postDate.date} ${postDate.monthAbr} ${postDate.fullyear} às ${postDate.hours}:${postDate.minutes}"

            mButtomLike.setOnLikeListener(likeListener)
            mTxtAdPostUserName.text = user.name


            if(post.type == "note") {
                mTxtAdPostText.text = KnifeParser.fromHtml(post.text?.replace("<br>", " ")).toString()
            }

            if(post.type == "thought") {
                mTxtAdPostText.text = KnifeParser.fromHtml(post.text)
            }

            if(post.type == "post") {
                mTxtAdPostText.text = KnifeParser.fromHtml(post.text)
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

            mLayoutUserProfile.setOnClickListener {
                val intent: Intent = Intent(mContext, UserProfileActivity::class.java)
                intent.putExtra("user", post.user)
                mContext.startActivity(intent)
            }

            mBtAdPostOptions.setOnClickListener(View.OnClickListener {
                val alertbox = AlertDialog.Builder(context)

                if(user.id == mUser.id)   {
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

        private fun updatePosts(post: Post, postLike: PostLike, pos: Int, action: String) {

            val fragTagPrefix = "android:switcher:${R.id.viewpager}"

            val manager = mActivity.supportFragmentManager
            val embassyPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:1")
            val allPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:2")

            if(embassyPostsfragment != null && embassyPostsfragment.isVisible) {
                val rootEmbassyListPost: EmbassyPostsFragment = embassyPostsfragment as EmbassyPostsFragment
                rootEmbassyListPost.updateLikes(post, postLike, action)
            }

            if(allPostsfragment != null && allPostsfragment.isVisible) {
                val rootAllListPost: AllPostsFragment = allPostsfragment as AllPostsFragment
                rootAllListPost.updateLikes(post, postLike, action)
            }
        }

        private fun removePosts(post: Post) {

            val fragTagPrefix = "android:switcher:${R.id.viewpager}"

            val manager = mActivity.supportFragmentManager
            val embassyPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:1")
            val allPostsfragment: Fragment? = manager.findFragmentByTag("$fragTagPrefix:2")

            if(embassyPostsfragment != null && embassyPostsfragment.isVisible) {
                val rootEmbassyListPost: EmbassyPostsFragment = embassyPostsfragment as EmbassyPostsFragment
                rootEmbassyListPost.removePost(post)
            }

            if(allPostsfragment != null && allPostsfragment.isVisible) {
                val rootAllListPost: AllPostsFragment = allPostsfragment as AllPostsFragment
                rootAllListPost.removePost(post)
            }
        }

        private fun confirmDialog(dialogTitle: String, dialogMessage: String, task: Task<Void>, position: Int) {
            AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Sim") { dialog, which ->
                    task.addOnCompleteListener {
                        removePosts(mPostList[position])
                    }.addOnFailureListener {
                        Log.d("EGVAPPLOG", it.message.toString())
                    }
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}