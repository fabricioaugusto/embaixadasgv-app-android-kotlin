package com.balloondigital.egvapp.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionConfig {

    companion object {
        
        fun validatePermission(permissions: List<String>, activity: Activity) : Boolean {

            if(Build.VERSION.SDK_INT >= 23) {
                val permissionList: MutableList<String> = mutableListOf()
                for (permission: String in permissions) {
                    //Check if permission is Granted
                    val checkPermission: Boolean = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                    if(!checkPermission) permissionList.add(permission)
                }
                if (permissionList.isEmpty()) return true
                ActivityCompat.requestPermissions(activity, permissionList.toTypedArray(), 1)
            }

            return true
        }
    }
}