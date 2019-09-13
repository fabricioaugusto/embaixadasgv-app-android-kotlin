package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.Embassy


class EmbassyListAdapter(embassyList: List<Embassy>): RecyclerView.Adapter<EmbassyListAdapter.EmbassyViewHolder>() {

    private val mEmbassyList: List<Embassy> = embassyList
    var onItemClick: ((embassy: Embassy, pos: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmbassyViewHolder {
        val context = parent.context

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_list_embassy, parent, false)

        return EmbassyViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return  mEmbassyList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: EmbassyViewHolder, position: Int) {
        val embassy: Embassy = mEmbassyList[position]
        holder.bindData(embassy)
    }

    inner class EmbassyViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {

        private val mTxtAdEmbassyName: TextView = itemView.findViewById(R.id.txtAdEmbassyName)
        private val mTxtAdEmbassyCity: TextView = itemView.findViewById(R.id.txtAdEmbassyCity)
        private val mTxtAdEmbassyInList: TextView = itemView.findViewById(R.id.txtAdEmbassyInList)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mEmbassyList[adapterPosition], adapterPosition)
            }
        }

        fun bindData(embassy: Embassy) {

            mTxtAdEmbassyInList.isGone = embassy.status != "released"

            mTxtAdEmbassyName.text = embassy.name
            mTxtAdEmbassyCity.text = "${embassy.city} - ${embassy.state_short}"
        }
    }
}