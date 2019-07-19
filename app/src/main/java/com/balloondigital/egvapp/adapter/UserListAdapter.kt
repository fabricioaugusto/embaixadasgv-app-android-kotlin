package com.balloondigital.egvapp.adapter

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = mUserList[position]
        holder.bindData(user)
    }

    inner class UserViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mUserProfileImage: CircleImageView = itemView.findViewById(R.id.imageUserRowProfile)
        val mTextViewName: TextView = itemView.findViewById(R.id.textUserRowName)
        val mTextViewEmail: TextView = itemView.findViewById(R.id.textUserRowEmail)


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mUserList[adapterPosition])
            }
        }

        fun bindData(user: User) {

            mTextViewName.text = user.name
            mTextViewEmail.text = user.status

            if(user.profile_img != null) {
                Glide.with(context)
                    .load(user.profile_img)
                    .into(mUserProfileImage)
            } else {
                if(user.gender == "F") {
                    mUserProfileImage.setImageResource(R.drawable.avatar_woman)
                }
            }
        }
    }

}