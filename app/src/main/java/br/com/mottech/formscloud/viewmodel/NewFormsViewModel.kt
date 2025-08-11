package br.com.mottech.formscloud.ui

import androidx.lifecycle.*
import br.com.mottech.formscloud.data.AppDatabase
import br.com.mottech.formscloud.data.FormSubmission
import br.com.mottech.formscloud.model.FormModel
import kotlinx.coroutines.launch

class NewFormsViewModel(private val database: AppDatabase) : ViewModel() {

    private val _formModel = MutableLiveData<FormModel>()
    val formModel: LiveData<FormModel> = _formModel
    private val _fieldValues = MutableLiveData<MutableMap<String, String>>(mutableMapOf())

    val isFormValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        var currentFieldValues = _fieldValues.value
        var currentFormModel = _formModel.value
        fun update() {
            val valid = currentFormModel?.fields?.filter { it.required == true }?.all { field ->
                val value = currentFieldValues?.get(field.name)
                !value.isNullOrEmpty() && value != "false"
            }
                ?: false
            this.value = valid
        }
        addSource(_fieldValues) {
            currentFieldValues = it
            update()
        }
        addSource(_formModel) {
            currentFormModel = it
            update()
        }
    }

    fun setFormModel(form: FormModel) {
        _formModel.value = form
    }

    fun updateFieldValue(fieldName: String, value: String) {
        val map = _fieldValues.value ?: mutableMapOf()
        map[fieldName] = value
        _fieldValues.value = map
    }

    fun saveSubmission(onSaved: () -> Unit) {
        val form = _formModel.value ?: return
        val values = _fieldValues.value ?: return

        viewModelScope.launch {
            val submission = FormSubmission(
                parentFormId = form.title,
                fieldValues = values
            )
            database.formSubmissionDao().insert(submission)
            onSaved()
        }
    }
}
