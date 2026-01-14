package com.anastasiaiva.pelmenisegodnya.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anastasiaiva.pelmenisegodnya.repository.KerilRepository
import com.anastasiaiva.pelmenisegodnya.update.UpdateRepository

@Suppress("UNCHECKED_CAST")
class KerilViewModelFactory(
    private val repository: KerilRepository, private val updateRepository: UpdateRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KerilViewModel::class.java)) {
            return KerilViewModel(
                repository = repository,
                updateRepository = updateRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}