package com.anastasiaiva.pelmenisegodnya.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.anastasiaiva.pelmenisegodnya.navigation.AppNavHost
import com.anastasiaiva.pelmenisegodnya.repository.KerilRepository
import com.anastasiaiva.pelmenisegodnya.update.ApkDownloadRepository
import com.anastasiaiva.pelmenisegodnya.update.UpdateRepository
import com.anastasiaiva.pelmenisegodnya.util.VersionUtils
import kotlinx.coroutines.launch
import java.io.File
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.anastasiaiva.pelmenisegodnya.ui.theme.PelmeniTheme

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: KerilViewModel

    private lateinit var apkDownloadRepository: ApkDownloadRepository

    private fun canInstallApk(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestInstallPermission() {
        val intent = Intent(
            android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            android.net.Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }


    private fun installApk(file: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("date_prefs", MODE_PRIVATE)
        val repository = KerilRepository(preferences)
        val updateRepository = UpdateRepository()
        apkDownloadRepository = ApkDownloadRepository(this)

        val factory = KerilViewModelFactory(
            repository = repository,
            updateRepository = updateRepository
        )
        viewModel = ViewModelProvider(this, factory)[KerilViewModel::class.java]

        viewModel.checkForUpdates(VersionUtils.getVersionCode(this))

        setContent {
            PelmeniTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onUpdateClick = {
                            lifecycleScope.launch {
                                val apkUrl = viewModel.getApkUrl()
                                if (apkUrl == null) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Не удалось получить ссылку обновления",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !canInstallApk()) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Разрешите установку из неизвестных источников",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    requestInstallPermission()
                                    return@launch
                                }

                                Toast.makeText(
                                    this@MainActivity,
                                    "Скачиваю обновление...",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val apkFile = apkDownloadRepository.downloadApk(apkUrl)
                                if (apkFile == null) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Ошибка загрузки APK",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }

                                installApk(apkFile)
                            }
                        })
                }
            }
        }
    }
}
