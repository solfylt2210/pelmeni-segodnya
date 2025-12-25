package com.anastasiaiva.pelmenisegodnya.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anastasiaiva.pelmenisegodnya.repository.KerilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import com.anastasiaiva.pelmenisegodnya.R


class KerilViewModel(private val repository: KerilRepository) : ViewModel() { //тут все ок, это DI: ViewModel нужен репозиторий, чтоб тянуть оттуда данные.

    val images = listOf( // тут тоже понятно, это список моих картинок
        R.drawable.slot_1,
        R.drawable.slot_2,
        R.drawable.slot_3,
        R.drawable.slot_4,
        R.drawable.slot_5,
        R.drawable.slot_6,
        R.drawable.slot_7,
        R.drawable.slot_8,
        R.drawable.slot_9,
        R.drawable.slot_10,
        R.drawable.slot_11,
        R.drawable.slot_12,
        R.drawable.slot_13,
        R.drawable.slot_14,
        R.drawable.slot_15,
        R.drawable.slot_16,
        R.drawable.slot_17,
        R.drawable.slot_18,
        R.drawable.slot_19,
        R.drawable.slot_20,
        R.drawable.slot_21,
        R.drawable.slot_22,
    )
    private val _uiState = MutableStateFlow<KerilUiState>(KerilUiState.Idle) // тут поняла, подписываемся на слежку за состоянием
    val uiState: StateFlow<KerilUiState> = _uiState // строчка нужна для безопасной передачи данных

    @RequiresApi(Build.VERSION_CODES.O)
    fun canShowResultToday( // логику функции поняла
        lastTimestamp: Long?,
        now: Instant
    ): Boolean {
        if (lastTimestamp == null) return true

        val lastDate = Instant.ofEpochMilli(lastTimestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val today = now
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return lastDate != today
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onButtonClicked() {
        val lastTimestamp = repository.getLastTimestamp()
        val now = Instant.now()
        if (!canShowResultToday(lastTimestamp, now)) {
            _uiState.value = KerilUiState.AlreadyUsedToday(
                imageResId = R.drawable.slot_main,
                message = "Я сегодня уже всё сказал"
            )
            return
        }
        _uiState.value = KerilUiState.Loading(images.random())

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < 3000) {
                _uiState.value = KerilUiState.Loading(
                    imageResId = images.random()
                )
                delay(10)
            }

            val finalImage = images.random()
            val phrase = repository.getRandomPhrase()

            repository.saveLastTimestamp(now.toEpochMilli())

            _uiState.value = KerilUiState.Result(
                imageResId = finalImage,
                phrase = phrase
            )
        }
    }
}


