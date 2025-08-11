package br.com.mottech.formscloud.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.mottech.formscloud.ui.NewFormsViewModel

class NewFormsViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewFormsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewFormsViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
