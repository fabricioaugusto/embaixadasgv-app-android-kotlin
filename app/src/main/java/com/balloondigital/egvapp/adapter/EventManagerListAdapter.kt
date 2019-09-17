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
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide

class EventManagerListAdapter(userList: List<Event>): RecyclerView.Adapter<EventManagerListAdapter.EventManagerViewHolder>() {

    private val mUserList: List<Event> = userList
    var onItemClick: ((event: Event) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventManagerViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_manager_event, parent, false)

        return EventManagerViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mUserList.size
    }

    override fun onBindViewHolder(holder: EventManagerViewHolder, position: Int) {
        val event: Event = mUserList[position]
        holder.bindData(event)
    }

    inner class EventManagerViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mEventCoverImage: ImageView = itemView.findViewById(R.id.imgAdEventCover)
        val mEventTheme: TextView = itemView.findViewById(R.id.txtAdEventTheme)
        val mEventMonthAbr: TextView = itemView.findViewById(R.id.txtAdEventMonthAbr)
        val mEventDate: TextView = itemView.findViewById(R.id.txtAdEventDate)
        val mEventEventTime: TextView = itemView.findViewById(R.id.txtAdEventTime)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mUserList[adapterPosition])
            }
        }

        fun bindData(event: Event) {
            val dateStr =  Converters.dateToString(event.date!!)

            mEventTheme.text = event.theme
            mEventMonthAbr.text = dateStr.monthAbr
            mEventDate.text = dateStr.date
            mEventEventTime.text = "${dateStr.weekday} às ${dateStr.hours}:${dateStr.minutes}"

            if(event.cover_img != null) {
                Glide.with(context)
                    .load(event.cover_img)
                    .into(mEventCoverImage)
            }
        }
    }
}