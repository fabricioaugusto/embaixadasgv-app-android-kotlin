package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.EmbassySponsor
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class SponsorManagerListAdapter(userList: List<EmbassySponsor>): RecyclerView.Adapter<SponsorManagerListAdapter.SponsorManagerViewHolder>() {

    private val mUserList: List<EmbassySponsor> = userList
    var onItemClick: ((sponsor: EmbassySponsor) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponsorManagerViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_list_user, parent, false)

        return SponsorManagerViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mUserList.size
    }

    override fun onBindViewHolder(holder: SponsorManagerViewHolder, position: Int) {
        val sponsor: EmbassySponsor = mUserList[position]
        holder.bindData(sponsor)
    }

    inner class SponsorManagerViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mUserProfileImage: CircleImageView = itemView.findViewById(R.id.imageUserRowProfile)
        val mTextViewName: TextView = itemView.findViewById(R.id.textUserRowName)
        val mTextViewEmail: TextView = itemView.findViewById(R.id.textUserRowEmail)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mUserList[adapterPosition])
            }
        }

        fun bindData(sponsor: EmbassySponsor) {

            mTextViewName.text = sponsor.name
            mTextViewEmail.text = "Padrinho de embaixadas"

            if(sponsor.user.profile_img != null) {
                Glide.with(context)
                    .load(sponsor.user.profile_img)
                    .into(mUserProfileImage)
            } else {
                mUserProfileImage.setImageResource(R.drawable.avatar)
            }
        }
    }
}