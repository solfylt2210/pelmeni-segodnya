package com.anastasiaiva.pelmenisegodnya.ui

sealed class KerilUiState {
    object Idle : KerilUiState()
    data class Loading(
        val imageResId: Int
    ) : KerilUiState()

    data class Result(
        val imageResId: Int,
        val phrase: String
    ) : KerilUiState()

    data class AlreadyUsedToday(
        val imageResId: Int,
        val message: String
    ) : KerilUiState()

    data class UpdateAvailable(
        val remoteVersion: Int
    ) : KerilUiState()
}