package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.balloondigital.egvapp.R

class CreatePostDialogAdapter(
    context: Context,
    private val isGrid: Boolean,
    private val count: Int
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var view: View? = convertView

        if (view == null) {
            view = if (isGrid) {
                layoutInflater.inflate(R.layout.adapter_dialog_grid, parent, false)
            } else {
                layoutInflater.inflate(R.layout.adapter_dialog_list, parent, false)
            }

            viewHolder = ViewHolder(
                view.findViewById(R.id.txtAdDialogLabel),
                view.findViewById(R.id.imgAdDialogIcon)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val context = parent.context
        when (position) {
            0 -> {
                viewHolder.textView.text = "Pensamento"
                viewHolder.imageView.setImageResource(R.drawable.ic_create_tought)
            }
            1 -> {
                viewHolder.textView.text = "Artigo"
                viewHolder.imageView.setImageResource(R.drawable.ic_create_article)
            }
            else -> {
                viewHolder.textView.text = "Foto com descrição"
                viewHolder.imageView.setImageResource(R.drawable.ic_create_picture)
            }
        }

        return view!!
    }

    data class ViewHolder(val textView: TextView, val imageView: ImageView)
}