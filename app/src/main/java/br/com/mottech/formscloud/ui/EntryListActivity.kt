package br.com.mottech.formscloud.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mottech.formscloud.adapter.OnItemInteractionListener
import br.com.mottech.formscloud.adapter.SubmissionListAdapter
import br.com.mottech.formscloud.data.AppDatabase
import br.com.mottech.formscloud.data.FormSubmission
import br.com.mottech.formscloud.databinding.ActivityEntryListBinding
import br.com.mottech.formscloud.model.FormModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class EntryListActivity : AppCompatActivity(), OnItemInteractionListener {

    private lateinit var binding: ActivityEntryListBinding
    private lateinit var formModel: FormModel
    private lateinit var submissionAdapter: SubmissionListAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getFormFromIntent()

        setupDatabase()
        setupViews()
        observeSubmissions()
    }

    private fun getFormFromIntent() {
        val form = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SELECTED_FORM", FormModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("SELECTED_FORM")
        }

        if (form == null) {
            finish()
            return
        }
        formModel = form
        title = formModel.title
    }

    private fun setupDatabase() {
        database = AppDatabase.getDatabase(this)
    }

    private fun setupViews() {
        submissionAdapter = SubmissionListAdapter(emptyList(), this)
        binding.recyclerMyForms.apply {
            layoutManager = LinearLayoutManager(this@EntryListActivity)
            adapter = submissionAdapter
        }

        binding.fabAddForms.setOnClickListener {
            val intent = Intent(this, NewFormsActivity::class.java).apply {
                putExtra("FORM_TO_FILL", formModel)
            }
            startActivity(intent)
        }
    }

    private fun observeSubmissions() {
        val formId = formModel.title

        database.formSubmissionDao().getSubmissionsForForm(formId).observe(this) { submissions ->
            updateUi(submissions)
        }
    }

    private fun updateUi(submissions: List<FormSubmission>) {
        if (submissions.isEmpty()) {
            binding.recyclerMyForms.visibility = View.GONE
         //   binding.textViewEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerMyForms.visibility = View.VISIBLE
         //   binding.textViewEmptyState.visibility = View.GONE
            submissionAdapter.updateData(submissions)
        }
    }

    override fun onItemClick(submission: FormSubmission) {
        val intent = Intent(this, SubmissionDetailActivity::class.java).apply {
            putExtra("SUBMISSION_DATA", submission)
            putExtra("FORM_MODEL", formModel)
        }
        startActivity(intent)    }

    override fun onItemLongClick(submission: FormSubmission) {
        showDeleteConfirmationDialog(submission)
    }

    private fun showDeleteConfirmationDialog(submission: FormSubmission) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm")
            .setMessage("Você tem certeza que deseja deletar esta entrada? Esta ação não pode ser desfeita.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                deleteSubmission(submission)
            }
            .show()
    }

    private fun deleteSubmission(submission: FormSubmission) {
        lifecycleScope.launch {
            database.formSubmissionDao().delete(submission)
        }
    }
}