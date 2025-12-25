package com.anastasiaiva.pelmenisegodnya.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class UpdateRepository {

    suspend fun loadRemoteVersion(): Int? {
        return withContext(Dispatchers.IO) {
            try {
                val text = URL(UPDATE_URL).readText()
                parseVersion(text)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun parseVersion(text: String): Int? {
        // ожидаем формат: version=2
        val parts = text.split("=")
        if (parts.size != 2) return null
        return parts[1].trim().toIntOrNull()
    }

    companion object {
        private const val UPDATE_URL =
            "https://raw.githubusercontent.com/solfylt2210/pelmeni-segodnya/refs/heads/main/update.txt"
    }
}