package br.com.mottech.formscloud.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mottech.formscloud.SubmissionDetailAdapter
import br.com.mottech.formscloud.data.FormSubmission
import br.com.mottech.formscloud.databinding.ActivitySubmissionDetailBinding
import br.com.mottech.formscloud.model.FormModel

class FormsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubmissionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val form = getParcelableExtra<FormModel>("FORM_MODEL")
        val submission = getParcelableExtra<FormSubmission>("SUBMISSION_DATA")

        if (form == null || submission == null) {
            finish()
            return
        }

        setupRecyclerView(form, submission)
    }
    
    private fun setupRecyclerView(form: FormModel, submission: FormSubmission) {
        binding.recyclerMyForm.layoutManager = LinearLayoutManager(this)
        binding.recyclerMyForm.adapter = SubmissionDetailAdapter(form.fields, submission.fieldValues)
    }
    
    private inline fun <reified T : android.os.Parcelable> getParcelableExtra(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> intent.getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") intent.getParcelableExtra(key) as? T
    }
}