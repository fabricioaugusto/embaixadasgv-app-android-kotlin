package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import java.util.*
import android.content.Context.LAYOUT_INFLATER_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.widget.TextView
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


class MenuListAdapter(context: Context, user: User) : BaseAdapter() {

    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1
    private val TYPE_PROFILE = 2
    private lateinit var mUser: User
    private val mData: MutableList<String> = mutableListOf()
    private val sectionHeader: TreeSet<Int> = TreeSet<Int>()

    private var mInflater: LayoutInflater? = null


    init {
        mInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mUser = user
    }

    fun addItem(item: String) {
        mData.add(item)
        notifyDataSetChanged()
    }

    fun addSectionHeaderItem(item: String) {
        mData.add(item)
        sectionHeader.add(mData.size - 1)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return when {
            position == 0 -> TYPE_PROFILE
            sectionHeader.contains(position) -> TYPE_SEPARATOR
            else -> TYPE_ITEM
        }
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var holder: ViewHolder? = null
        val rowType = getItemViewType(position)

        var view: View? = convertView

        if (view == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    view = mInflater!!.inflate(R.layout.adapter_menu_section, null)
                    holder.textView = view.findViewById(R.id.text) as TextView

                }
                TYPE_SEPARATOR -> {
                    view = mInflater!!.inflate(R.layout.adapter_menu_item, null)
                    holder.textView = view.findViewById(R.id.textSeparator) as TextView
                }
                TYPE_PROFILE -> {
                    view = mInflater!!.inflate(R.layout.adapter_menu_profile, null)
                    holder.mProfilePhoto = view.findViewById(R.id.imgAdMenuProfile)
                    holder.mProfileName = view.findViewById(R.id.txtAdMenuProfile)
                    holder.textView = view.findViewById(R.id.text) as TextView
                    Glide.with(view.context)
                        .load(mUser.profile_img)
                        .into(holder.mProfilePhoto!!)

                        holder.mProfileName!!.text = mUser.name
                }
            }

            view!!.tag = holder
        } else {
            view = convertView
            holder = view!!.tag as ViewHolder
        }

        holder.textView!!.text = mData[position]
        return view
    }

    override fun getItem(position: Int): String {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mData.size
    }

    class ViewHolder {
        var textView: TextView? = null
        var mProfilePhoto: CircleImageView? = null
        var mProfileName: TextView? = null
    }
}