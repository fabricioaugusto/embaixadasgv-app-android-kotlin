package com.balloondigital.egvapp.utils

import android.content.Context
import android.net.NetworkInfo
import android.net.ConnectivityManager

class AppStatus private constructor(){

    companion object {

        fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

            return activeNetwork?.isConnectedOrConnecting == true
        }
    }
}