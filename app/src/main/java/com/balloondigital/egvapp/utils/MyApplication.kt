package com.balloondigital.egvapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class MyApplication private constructor(){
    object util {
        fun openExternalLink(context: Context, url: String) {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }
    object const {
        object urls {
            val googlePlayUrl = "https://play.google.com/store/apps/details?id=com.balloondigital.egvapp"
            val policyPrivacy = "https://embaixadasgv.app/politicas-de-privacidade"
        }
    }
}