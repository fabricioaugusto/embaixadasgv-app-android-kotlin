package com.balloondigital.egvapp.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.BasicEmbassy
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.Sponsor
import com.google.android.apps.gmm.map.util.jni.NativeHelper.context
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder


class SponsorListAdapter(groups: List<ExpandableGroup<BasicEmbassy>>) :
    ExpandableRecyclerViewAdapter<SponsorListAdapter.SponsorViewHolder, SponsorListAdapter.BasicEmbassyViewHolder>(groups) {

    var onItemClick: ((embassy: BasicEmbassy) -> Unit)? = null

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): SponsorViewHolder {
        val context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_sponsor_list_header, parent, false)
        return SponsorViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): BasicEmbassyViewHolder {
        val context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_list_embassy, parent, false)
        return BasicEmbassyViewHolder(view)
    }

    override fun onBindChildViewHolder(
        holder: BasicEmbassyViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val embassy: BasicEmbassy = (group).items?.get(childIndex) as BasicEmbassy
        holder.embassy = embassy
        holder.onBind(embassy = embassy)
    }

    override fun onBindGroupViewHolder(
        holder: SponsorViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        holder.setGenreTitle(group)
    }

    inner class SponsorViewHolder(itemView: View) : GroupViewHolder(itemView) {

        val mTxtSponsorText: TextView = itemView.findViewById(R.id.txtAdSponsorName)
        val mTxtSponsorEmbassyCount: TextView = itemView.findViewById(R.id.txtAdSponsorEmbassyCount)

        fun setGenreTitle(group: ExpandableGroup<*>) {
            mTxtSponsorText.text = group.title
            mTxtSponsorEmbassyCount.text = "(${group.items.size})"
        }
    }

    inner class BasicEmbassyViewHolder(itemView: View) : ChildViewHolder(itemView) {

        lateinit var embassy: BasicEmbassy
        val mTxtAdEmbassyName: TextView = itemView.findViewById(R.id.txtAdEmbassyName)
        val mTxtAdEmbassyCity: TextView = itemView.findViewById(R.id.txtAdEmbassyCity)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(embassy)
            }
        }

        fun onBind(embassy: BasicEmbassy) {
            mTxtAdEmbassyName.text = embassy.name
            mTxtAdEmbassyCity.text = "${embassy.city} - ${embassy.state_short}"
        }
    }
}


