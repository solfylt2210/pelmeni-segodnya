package com.anastasiaiva.pelmenisegodnya.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

object VersionUtils {

    @RequiresApi(Build.VERSION_CODES.P)
    fun getVersionCode(context: Context): Int {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            0
        )
        return packageInfo.longVersionCode.toInt()
    }

    fun getVersionName(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            0
        )
        return packageInfo.versionName ?: "unknown"
    }
}