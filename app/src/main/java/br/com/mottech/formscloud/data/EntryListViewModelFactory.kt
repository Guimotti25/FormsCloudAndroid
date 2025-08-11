package br.com.mottech.formscloud.data // Use o mesmo pacote do seu ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.mottech.formscloud.data.FormRepository
import br.com.mottech.formscloud.viewmodel.EntryListViewModel

class EntryListViewModelFactory(
    private val repository: FormRepository,
    private val formId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryListViewModel(repository, formId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}