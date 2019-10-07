package com.balloondigital.egvapp.adapter

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(userList: List<User>): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val mUserList: List<User> = userList
    var onItemClick: ((user: User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.adapter_list_user, parent, false)

        return UserViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mUserList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = mUserList[position]
        holder.bindData(user)
    }

    inner class UserViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mUserIdentifer: TextView = itemView.findViewById(R.id.txtUserIdentifier)
        val mUserProfileImage: ImageView = itemView.findViewById(R.id.imageUserRowProfile)
        val mTextViewName: TextView = itemView.findViewById(R.id.textUserRowName)
        val mTextViewEmail: TextView = itemView.findViewById(R.id.textUserRowEmail)


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mUserList[adapterPosition])
            }
        }

        fun bindData(user: User) {

            mTextViewName.text = user.name

            if (user.occupation.isNullOrEmpty()){
                mTextViewEmail.text = "Eu sou GV!"
            } else {
                mTextViewEmail.text = user.occupation
            }

            val requestOptions: RequestOptions = RequestOptions()
            val options = requestOptions.transforms(CenterCrop(), RoundedCorners(120))

            if(user.profile_img != null) {
                Glide.with(context)
                    .load(user.profile_img)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(mUserProfileImage)
            } else {
                if(user.gender == "female") {
                    mUserProfileImage.setImageResource(R.drawable.avatar_woman)
                } else {
                    mUserProfileImage.setImageResource(R.drawable.avatar)
                }
            }

            if(user.leader) {
                mUserIdentifer.text = "Líder"
                mUserIdentifer.background = itemView.resources.getDrawable(R.drawable.bg_leader_identifier)
            }

            if(user.sponsor) {
                if(user.gender == "female") {
                    mUserIdentifer.text = "Madrinha"
                } else {
                    mUserIdentifer.text = "Padrinho"
                }
                mUserIdentifer.background = itemView.resources.getDrawable(R.drawable.bg_sponsor_identifier)
            }

            mUserIdentifer.isGone = !(user.sponsor || user.leader)
        }
    }
}