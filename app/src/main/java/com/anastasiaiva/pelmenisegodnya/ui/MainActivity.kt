package com.anastasiaiva.pelmenisegodnya.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.anastasiaiva.pelmenisegodnya.databinding.ActivityMainBinding
import com.anastasiaiva.pelmenisegodnya.repository.KerilRepository
import com.anastasiaiva.pelmenisegodnya.update.ApkDownloadRepository
import com.anastasiaiva.pelmenisegodnya.update.UpdateRepository
import com.anastasiaiva.pelmenisegodnya.util.VersionUtils
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // создаем объект, который прочитает XML и заберет оттуда нужное для UI, инициализация при первом обращении
    private lateinit var viewModel: KerilViewModel // создаем объект "управляющего логикой", инициализация при первом обращении

    private var isApkDownloaded = false

    fun installApk(file: File) {
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
    override fun onCreate(savedInstanceState: Bundle?) { // метод переопределен из Context, что делается при создании/пересоздании Activity
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)// инициализируем переменную binding
        setContentView(binding.root) // говорим, что корневая тема в XML, смотри оттуда

        val versionCode = VersionUtils.getVersionCode(this)
        val versionName = VersionUtils.getVersionName(this)

        binding.updateButton.setOnClickListener {
            val apkFile = File(cacheDir, "update.apk")

            if (!apkFile.exists()) {
                Toast.makeText(this, "Файл обновления не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            installApk(apkFile)
        }


        val preferences = getSharedPreferences( // забираем из системы данные, в т.ч. о дате
            "date_prefs",
            MODE_PRIVATE
        )
        val repository = KerilRepository(preferences)
        val updateRepository = UpdateRepository()

        val factory = KerilViewModelFactory(
            repository = repository,
            updateRepository = updateRepository
        )
        viewModel = ViewModelProvider(this, factory)[KerilViewModel::class.java] // вот тут когда зависимости добавлены, инициализируем объект viewModel. Это все еще для меня сложновато, но в общих чертах понимаю.

        viewModel.checkForUpdates(
            currentVersionCode = VersionUtils.getVersionCode(this)
        )

        binding.button.setOnClickListener {
            viewModel.onButtonClicked()
        }


        fun animateResultImage() {
            binding.image.apply {
                scaleX = 1f
                scaleY = 1f
                animate()
                    .scaleX(1.5f)
                    .scaleY(1.5f)
                    .setDuration(300)
                    .start()
            }
        }

        lifecycleScope.launch { // запускаем корутину, пока жив Activity?
            repeatOnLifecycle(Lifecycle.State.STARTED) { // работаем, пока экран жив
                viewModel.uiState.collect { state -> // это уже ты писал, я понимаю суть, но в деталях не особо
                    when (state) { // это типа switch-case, тут все ясно

                        is KerilUiState.Idle -> {
                            // начальное состояние
                            binding.image.visibility = View.GONE
                            binding.resultText.visibility = View.GONE
                            binding.updateButton.visibility = View.GONE
                        }

                        is KerilUiState.Loading -> { //"вот че делаем, если состояние такое-то"
                            binding.button.isEnabled = false //кнопку нельзя жмать
                            // показываем "крутилку" / картинку
                                binding.image.visibility = View.VISIBLE
                                binding.image.setImageResource(state.imageResId)
                                binding.resultText.visibility = View.GONE
                                binding.updateButton.visibility = View.GONE
                        }

                        is KerilUiState.Result -> {
                            binding.button.isEnabled = true
                            // показываем результат
                            binding.image.visibility = View.VISIBLE
                            binding.image.setImageResource(state.imageResId)

                            binding.resultText.visibility = View.VISIBLE
                            binding.updateButton.visibility = View.GONE
                            binding.resultText.text = state.phrase
                            animateResultImage()
                        }

                        is KerilUiState.AlreadyUsedToday -> {
                            binding.image.visibility = View.VISIBLE
                            binding.image.setImageResource(state.imageResId)

                            binding.resultText.visibility = View.VISIBLE
                            binding.updateButton.visibility = View.GONE
                            binding.resultText.text = state.message

                            animateResultImage()
                        }

                        is KerilUiState.UpdateAvailable -> {
                            binding.updateButton.visibility = View.VISIBLE

                            if (!isApkDownloaded) {
                                isApkDownloaded = true

                                lifecycleScope.launch {
                                    val downloader = ApkDownloadRepository(this@MainActivity)

                                    val file = downloader.downloadApk(
                                        "https://github.com/solfylt2210/pelmeni-segodnya/releases/download/v1.0/app-release.apk"
                                    )

                                    if (file != null) {
                                        println("APK скачан: ${file.absolutePath}")
                                    } else {
                                        println("Ошибка скачивания APK")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}