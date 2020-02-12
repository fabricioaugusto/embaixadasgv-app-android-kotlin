package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.UserBasic
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions


class UserBasicAdapter(userList: List<UserBasic>): RecyclerView.Adapter<UserBasicAdapter.UserViewHolder>() {

    private val mUserList: List<UserBasic> = userList
    var onItemClick: ((user: UserBasic, pos: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.adapter_user_basic, parent, false)

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
        val user: UserBasic = mUserList[position]
        holder.bindData(user)
    }

    inner class UserViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mUserProfileImage: ImageView = itemView.findViewById(R.id.imageUserAdProfile)
        val mTextViewName: TextView = itemView.findViewById(R.id.textUserAdName)
        val mTextViewDescription: TextView = itemView.findViewById(R.id.textUserAdDescription)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mUserList[adapterPosition], adapterPosition)
            }
        }

        fun bindData(user: UserBasic) {

            mTextViewName.text = user.name
            mTextViewDescription.text = user.description
            
            val requestOptions: RequestOptions = RequestOptions()
            val options = requestOptions.transforms(CenterCrop(), RoundedCorners(120))

            if(user.profile_img != null) {
                Glide.with(context)
                    .load(user.profile_img)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(mUserProfileImage)
            } else {
                mUserProfileImage.setImageResource(R.drawable.avatar)
            }

        }
    }
}