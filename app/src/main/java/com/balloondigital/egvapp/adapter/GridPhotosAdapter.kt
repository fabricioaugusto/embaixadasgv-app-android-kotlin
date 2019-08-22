package com.balloondigital.egvapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.utils.SquareImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

class GridPhotosAdapter(context: Context, resource: Int, photosURL: MutableList<String>) :
    BaseAdapter() {

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
            holder.mImgAdEmbassyPhoto = view.findViewById(R.id.imgAdEmbassyPhoto) as SquareImageView
            holder.mPbAdEmbassyPhoto = view.findViewById(R.id.pbAdEmbassyPhoto) as ProgressBar
            holder.mImgAdEmbassyPhoto.setImageDrawable(view.resources.getDrawable(R.drawable.bg_upload_image))
            view!!.tag = holder
        } else {
            view = convertView
            holder = view!!.tag as ViewHolder
        }

        Glide.with(view.context)
            .load(mPhotosUrl[position])
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.mPbAdEmbassyPhoto.isGone = true
                    return false
                }
            })
            .into(holder.mImgAdEmbassyPhoto)
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
        lateinit var mImgAdEmbassyPhoto: SquareImageView
        lateinit var mPbAdEmbassyPhoto: ProgressBar
    }
}