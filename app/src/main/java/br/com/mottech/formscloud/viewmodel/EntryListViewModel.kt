package br.com.mottech.formscloud.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mottech.formscloud.data.FormRepository
import br.com.mottech.formscloud.data.FormSubmission
import kotlinx.coroutines.launch

class EntryListViewModel(
    private val repository: FormRepository,
    private val formId: String
) : ViewModel() {

    val submissions: LiveData<List<FormSubmission>> = repository.getSubmissionsForForm(formId)

    fun deleteSubmission(submission: FormSubmission) {
        viewModelScope.launch {
            repository.deleteSubmission(submission)
        }
    }
}