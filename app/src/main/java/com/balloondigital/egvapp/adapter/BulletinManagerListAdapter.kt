package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.Bulletin

class BulletinManagerListAdapter(bulletinList: List<Bulletin>): RecyclerView.Adapter<BulletinManagerListAdapter.BulletinManagerViewHolder>() {

    private val mBulletinList: List<Bulletin> = bulletinList
    var onItemClick: ((bulletin: Bulletin) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletinManagerViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_manager_bulletin, parent, false)

        return BulletinManagerViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mBulletinList.size
    }

    override fun onBindViewHolder(holder: BulletinManagerViewHolder, position: Int) {
        val bulletin: Bulletin = mBulletinList[position]
        holder.bindData(bulletin)
    }

    inner class BulletinManagerViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        val mBulletinTitle: TextView = itemView.findViewById(R.id.txtAdBulletinTitle)
        val mBulletinDescription: TextView = itemView.findViewById(R.id.txtAdBulletinDescription)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mBulletinList[adapterPosition])
            }
        }

        fun bindData(bulletin: Bulletin) {

            mBulletinTitle.text = bulletin.title
            mBulletinDescription.text = bulletin.resume

        }
    }
}