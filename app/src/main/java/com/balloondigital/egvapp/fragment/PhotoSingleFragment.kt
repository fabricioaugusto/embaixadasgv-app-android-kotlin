package com.balloondigital.egvapp.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.model.User
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.nostra13.universalimageloader.core.ImageLoader

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PhotoSingleFragment : Fragment() {

    private lateinit var mPvSingleImage: PhotoView
    private lateinit var mPhotoUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_photo_single, container, false)

        mPvSingleImage = view.findViewById(R.id.pvSingleImage)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mPhotoUrl = bundle.getString("photoUrl").toString()

            Glide.with(view.context)
                .load(mPhotoUrl)
                .into(mPvSingleImage)
        }

        return view
    }


}
