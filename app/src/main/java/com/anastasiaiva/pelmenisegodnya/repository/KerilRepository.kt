package com.anastasiaiva.pelmenisegodnya.repository

import android.content.SharedPreferences
import androidx.core.content.edit

class KerilRepository (private val preferences: SharedPreferences) {
    private val phrases = listOf(
        "Жопа с ручкой 💅",
        "Псина сутулая 😐",
        "Подзалупный творожок 🔥",
        "Говёшка на ладошке 💩",
        "Хуйня из-под коня \uD83D\uDC34",
        "Срань господня \uD83D\uDE4F",
        "Пиздец в обёртке \uD83C\uDF81",
        "Говно на палке \uD83E\uDEB5",
        "Хуйня обыкновенная \uD83D\uDCE6",
        "Хуйня липучая \uD83E\uDEB0",
        "Жопа на опыте \uD83D\uDE0E",
        "Чепушила очёрская \uD83D\uDE0F",
        "Петушара паршивая \uD83D\uDC14",
        "Гнидыч лохматый \uD83E\uDEE1",
        "Презик дырявый \uD83D\uDC4C",
        "Сопля сухая \uD83E\uDD0C",
        "Микропенис \uD83E\uDD0F",
        "Иуда волосатая \uD83E\uDD73",
        "Черт косматый \uD83E\uDD28"
    )

    private companion object {
        private const val KEY_LAST_RESULT_TIMESTAMP = "last_result_timestamp"
    }


    fun getRandomPhrase(): String {
        return phrases.random()
    }

fun saveLastTimestamp(timestamp: Long) {
    preferences.edit {
        putLong(KEY_LAST_RESULT_TIMESTAMP, timestamp)
    }
}

    fun getLastTimestamp(): Long? {
        return if (preferences.contains(KEY_LAST_RESULT_TIMESTAMP)) {
            preferences.getLong(KEY_LAST_RESULT_TIMESTAMP, 0L)
        } else {
            null
        }
    }
}
