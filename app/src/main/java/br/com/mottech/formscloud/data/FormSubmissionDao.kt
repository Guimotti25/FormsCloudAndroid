package br.com.mottech.formscloud.data // Use o seu nome de pacote

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FormSubmissionDao {
    @Insert
    suspend fun insert(submission: FormSubmission)

    @Delete
    suspend fun delete(submission: FormSubmission)

    @Query("SELECT * FROM form_submissions WHERE parentFormId = :formId ORDER BY createdAt DESC")
    fun getSubmissionsForForm(formId: String): LiveData<List<FormSubmission>>
}