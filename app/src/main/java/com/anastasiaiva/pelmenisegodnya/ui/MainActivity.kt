package com.anastasiaiva.pelmenisegodnya.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.anastasiaiva.pelmenisegodnya.databinding.ActivityMainBinding

import com.anastasiaiva.pelmenisegodnya.repository.KerilRepository


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // создаем объект, который прочитает XML и заберет оттуда нужное для UI, инициализация при первом обращении
    private lateinit var viewModel: KerilViewModel // создаем объект "управляющего логикой", инициализация при первом обращении
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) { // метод переопределен из Context, что делается при создании/пересоздании Activity
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)// инициализируем переменную binding
        setContentView(binding.root) // говорим, что корневая тема в XML, смотри оттуда

        val preferences = getSharedPreferences( // забираем из системы данные, в т.ч. о дате
            "date_prefs",
            MODE_PRIVATE
        )
        val repository = KerilRepository(preferences) // создаем объект репо, передаем туда системную инфу, в т.ч. дату
        val factory = KerilViewModelFactory(repository) //DI, не совсем помню, зачем мы это добавили. В общих чертах, чтобы вручную не внедрять зависимости в Activity
        viewModel = ViewModelProvider(this, factory)[KerilViewModel::class.java] // вот тут когда зависимости добавлены, инициализируем объект viewModel. Это все еще для меня сложновато, но в общих чертах понимаю.

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
                        }

                        is KerilUiState.Loading -> { //"вот че делаем, если состояние такое-то"
                            binding.button.isEnabled = false //кнопку нельзя жмать
                            // показываем "крутилку" / картинку
                                binding.image.visibility = View.VISIBLE
                                binding.image.setImageResource(state.imageResId)
                                binding.resultText.visibility = View.GONE
                        }

                        is KerilUiState.Result -> {
                            binding.button.isEnabled = true
                            // показываем результат
                            binding.image.visibility = View.VISIBLE
                            binding.image.setImageResource(state.imageResId)

                            binding.resultText.visibility = View.VISIBLE
                            binding.resultText.text = state.phrase
                            animateResultImage()
                        }

                        is KerilUiState.AlreadyUsedToday -> {
                            binding.image.visibility = View.VISIBLE
                            binding.image.setImageResource(state.imageResId)

                            binding.resultText.visibility = View.VISIBLE
                            binding.resultText.text = state.message

                            animateResultImage()
                        }
                    }
                }
            }
        }
    }
}