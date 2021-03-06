package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.Notification
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.github.mthli.knife.KnifeParser

class NotificationListAdapter(notificationList: List<Notification>): RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder>() {

    private val mNotificationList: List<Notification> = notificationList
    var onItemClick: ((user: Notification) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.adapter_list_notifications, parent, false)

        return NotificationViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mNotificationList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val user: Notification = mNotificationList[position]
        holder.bindData(user)
    }

    inner class NotificationViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mImgNotificationProfile: ImageView = itemView.findViewById(R.id.imgAdNotificationProfile)
        val mTxtNotificationTitle: TextView = itemView.findViewById(R.id.txtAdNotificationTitle)
        val mTxtNotificationDate: TextView = itemView.findViewById(R.id.txtAdNotificationDate)
        val mLayoutAdNotificationItem: LinearLayout = itemView.findViewById(R.id.layoutAdNotificationItem)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mNotificationList[adapterPosition])
            }
        }

        fun bindData(notification: Notification) {

            mTxtNotificationTitle.text = KnifeParser.fromHtml(notification.title)

            val notificationDate = Converters.dateToString(notification.created_at!!)
            mTxtNotificationDate.text = "${notificationDate.date} ${notificationDate.monthAbr} ${notificationDate.fullyear} às ${notificationDate.hours}:${notificationDate.minutes}"

            if(!notification.read) {
                mLayoutAdNotificationItem.setBackgroundColor(itemView.resources.getColor(R.color.colorActiveNotification))
            }

            val requestOptions: RequestOptions = RequestOptions()
            val options = requestOptions.transforms(CenterCrop(), RoundedCorners(120))

            if(notification.picture.isNotEmpty()) {
                Glide.with(context)
                    .load(notification.picture)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(mImgNotificationProfile)
            } else {

                Glide.with(context)
                    .load("https://firebasestorage.googleapis.com/v0/b/egv-app-f851e.appspot.com/o/assets%2Fimages%2Fbg_egv_logo.png?alt=media&token=90971d90-b517-47c5-a3c8-cede129cba3e")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(mImgNotificationProfile)
            }
            
        }
    }
}