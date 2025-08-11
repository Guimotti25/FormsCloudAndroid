package br.com.mottech.formscloud.data

import androidx.lifecycle.LiveData

class FormRepository(private val formSubmissionDao: FormSubmissionDao) {

    suspend fun insertSubmission(submission: FormSubmission) {
        formSubmissionDao.insert(submission)
    }

    suspend fun deleteSubmission(submission: FormSubmission) {
        formSubmissionDao.delete(submission)
    }

    fun getSubmissionsForForm(formId: String): LiveData<List<FormSubmission>> {
        return formSubmissionDao.getSubmissionsForForm(formId)
    }
}