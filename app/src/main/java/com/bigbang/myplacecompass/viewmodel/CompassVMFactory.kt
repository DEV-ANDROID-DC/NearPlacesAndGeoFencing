package com.bigbang.myplacecompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bigbang.myplacecompass.repository.PlacesRepository

class CompassVMFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompassViewModel (PlacesRepository) as T
    }
}