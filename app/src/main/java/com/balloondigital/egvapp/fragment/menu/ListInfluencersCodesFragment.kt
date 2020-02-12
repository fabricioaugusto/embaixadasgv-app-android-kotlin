package com.balloondigital.egvapp.fragment.menu


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.balloondigital.egvapp.R

/**
 * A simple [Fragment] subclass.
 */
class ListInfluencersCodesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_influencers_codes, container, false)
    }


}
