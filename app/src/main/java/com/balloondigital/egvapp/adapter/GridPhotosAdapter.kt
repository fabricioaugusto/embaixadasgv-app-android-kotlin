package com.balloondigital.egvapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.balloondigital.egvapp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nostra13.universalimageloader.core.ImageLoader

class GridPhotosAdapter(context: Context, resource: Int, photosURL: MutableList<String>) :
    ArrayAdapter<String>(context, resource, photosURL) {

    private val mContext: Context = context
    private val mPhotosUrl: MutableList<String> = photosURL
    private var mInflater: LayoutInflater? = null

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var holder: ViewHolder? = null
        val rowType = getItemViewType(position)

        var view: View? = convertView

        if (view == null) {
            holder = ViewHolder()
            view = mInflater!!.inflate(R.layout.adapter_grid_photo, null)
            holder.mImgAdEmbassyPhoto = view.findViewById(R.id.imgAdEmbassyPhoto) as ImageView
            view!!.tag = holder
        } else {
            view = convertView
            holder = view!!.tag as ViewHolder
        }

        ImageLoader.getInstance().displayImage(mPhotosUrl[position], holder.mImgAdEmbassyPhoto!!)

        return view
    }

    override fun getItem(position: Int): String {
        return mPhotosUrl[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mPhotosUrl.size
    }

    class ViewHolder {
        var mImgAdEmbassyPhoto: ImageView? = null
    }
}