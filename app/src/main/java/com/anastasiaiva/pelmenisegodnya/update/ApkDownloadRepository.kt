package com.anastasiaiva.pelmenisegodnya.update

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ApkDownloadRepository(
    private val context: Context
) {

    suspend fun downloadApk(apkUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val apkFile = File(context.cacheDir, APK_FILE_NAME)

                URL(apkUrl).openStream().use { input ->
                    apkFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                apkFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    companion object {
        private const val APK_FILE_NAME = "update.apk"
    }
}
