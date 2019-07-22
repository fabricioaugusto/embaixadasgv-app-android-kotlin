package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.Event
import com.bumptech.glide.Glide

class EventListAdapter(userList: List<Event>): RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private val mUserList: List<Event> = userList
    var onItemClick: ((event: Event) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.adapter_list_event, parent, false)

        return EventViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mUserList.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event: Event = mUserList[position]
        holder.bindData(event)
    }

    inner class EventViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mEventCoverImage: ImageView = itemView.findViewById(R.id.imgAdEventCover)
        val mEventTheme: TextView = itemView.findViewById(R.id.txtAdEventTheme)
        val mEventTag: TextView = itemView.findViewById(R.id.txtAdEventTag)
        val mEventPlace: TextView = itemView.findViewById(R.id.txtAdEventPlace)


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mUserList[adapterPosition])
            }
        }

        fun bindData(event: Event) {

            mEventTheme.text = event.theme
            mEventPlace.text = event.place
            mEventTag.text = event.tag

            if(event.cover_img != null) {
                Glide.with(context)
                    .load(event.cover_img)
                    .into(mEventCoverImage)
            }
        }
    }
}