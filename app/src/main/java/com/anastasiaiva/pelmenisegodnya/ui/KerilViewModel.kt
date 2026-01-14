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
import com.anastasiaiva.pelmenisegodnya.update.UpdateRepository
import com.anastasiaiva.pelmenisegodnya.util.VersionUtils


class KerilViewModel(private val updateRepository: UpdateRepository, private val repository: KerilRepository) : ViewModel() {

    val images = listOf(
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
        R.drawable.slot_30,
        R.drawable.slot_31,
        R.drawable.slot_32,
        R.drawable.slot_33,
        R.drawable.slot_34,
        R.drawable.slot_35,
        R.drawable.slot_36,
        R.drawable.slot_37,
        R.drawable.slot_38,
        R.drawable.slot_39,
        R.drawable.slot_40
    )
    private val _uiState = MutableStateFlow<KerilUiState>(KerilUiState.Idle)
    val uiState: StateFlow<KerilUiState> = _uiState

    @RequiresApi(Build.VERSION_CODES.O)
    fun canShowResultToday(
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
    private val _isUpdateAvailable = MutableStateFlow(false)
    val isUpdateAvailable: StateFlow<Boolean> = _isUpdateAvailable
    private var remoteVersion: Int? = null

    fun checkForUpdates(currentVersionCode: Int) {
        viewModelScope.launch {
            val version = updateRepository.loadRemoteVersion() ?: return@launch
            remoteVersion = version
            _isUpdateAvailable.value = version > currentVersionCode
        }
    }

    fun getApkUrl(): String? {
        val version = remoteVersion ?: return null
        return "https://github.com/solfylt2210/pelmeni-segodnya/releases/download/v$version/app-release.apk"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onButtonClicked() {
        val lastTimestamp = repository.getLastTimestamp()
        val now = Instant.now()
//        if (!canShowResultToday(lastTimestamp, now)) {
//            _uiState.value = KerilUiState.AlreadyUsedToday(
//                imageResId = R.drawable.slot_main,
//                message = "Я сегодня уже всё сказал"
//            )
//            return
//        }
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
            val endingPhrase = repository.getRandomEndingPhrase()

            repository.saveLastTimestamp(now.toEpochMilli())

            _uiState.value = KerilUiState.Result(
                imageResId = finalImage,
                phrase = phrase,
                endingPhrase = endingPhrase
            )
        }
    }
}


