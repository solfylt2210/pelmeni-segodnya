package com.anastasiaiva.pelmenisegodnya.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anastasiaiva.pelmenisegodnya.repository.KerilRepository

class KerilViewModelFactory(
    private val repository: KerilRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KerilViewModel::class.java)) {
            return KerilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}