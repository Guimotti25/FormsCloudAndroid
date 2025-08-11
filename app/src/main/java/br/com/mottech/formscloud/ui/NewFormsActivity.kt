package br.com.mottech.formscloud.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import br.com.mottech.formscloud.data.AppDatabase
import br.com.mottech.formscloud.data.FormSubmission
import br.com.mottech.formscloud.databinding.ActivityNewFormsBinding
import br.com.mottech.formscloud.helper.PrimaryButton
import br.com.mottech.formscloud.model.Field
import br.com.mottech.formscloud.model.FormModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewFormsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewFormsBinding
    private lateinit var formModel: FormModel
    private lateinit var database: AppDatabase
    private val fieldValues = mutableMapOf<String, String>()
    private var saveButton: PrimaryButton? = null
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private var currentFileTextView: TextView? = null
    private var currentFileFieldName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try
        {
            binding = ActivityNewFormsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->

                        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        try {
                            contentResolver.takePersistableUriPermission(uri, takeFlags)
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }

                        currentFileFieldName?.let { fieldName ->
                            val fileName = getFileName(uri)
                            currentFileTextView?.text = fileName
                            fieldValues[fieldName] = uri.toString()
                            validateForm()

                            currentFileFieldName = null
                            currentFileTextView = null
                        }
                    }
                }
            }

            getFormFromIntent()
            database = AppDatabase.getDatabase(this)
            generateForm()
            validateForm()
        }
        catch (e: Exception)
        {
           e.printStackTrace()
        }
    }

    private fun getFormFromIntent() {
        val form = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("FORM_TO_FILL", FormModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("FORM_TO_FILL")
        }
        if (form == null) {
            finish(); return
        }
        formModel = form
        title = "New - ${formModel.title}"
    }

    private fun generateForm() {
        val container = binding.formContainer

        formModel.fields.forEach { field ->
            val label = createLabel(field)
            container.addView(label)

            val inputView = createInputView(field)
            container.addView(inputView)
        }

        val button = PrimaryButton(this).apply {
            text = "Save"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 48 }

            setOnClickListener {
                lifecycleScope.launch {
                    val submission = FormSubmission(
                        parentFormId = formModel.title,
                        fieldValues = fieldValues
                    )
                    database.formSubmissionDao().insert(submission)
                    finish()
                }
            }
        }
        this.saveButton = button
        container.addView(button)
    }

    private fun createLabel(field: Field): TextView {
        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = if (field.type != "checkbox") 32 else 16 }

            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
        }

        if (field.required == true) {
            val spannable = SpannableStringBuilder(field.label + " *")
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.holo_red_dark)),
                spannable.length - 1,
                spannable.length,
                0
            )
            textView.text = spannable
        } else {
            textView.text = field.label
        }
        return textView
    }

    private fun createInputView(field: Field): View {
        when (field.type) {
            "text", "email", "number", "password" -> {
                val layout = TextInputLayout(this)
                val editText = com.google.android.material.textfield.TextInputEditText(this).apply {
                    when (field.type) {
                        "email" -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        "number" -> inputType = InputType.TYPE_CLASS_NUMBER
                        "password" -> inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        "textarea" -> { minLines = 3; isSingleLine = false }
                    }
                    doOnTextChanged { text, _, _, _ ->
                        fieldValues[field.name] = text.toString()
                        validateForm()
                    }
                }
                layout.addView(editText)
                return layout
            }
            "description" -> {
                return TextView(this).apply {
                    text = HtmlCompat.fromHtml(field.label, HtmlCompat.FROM_HTML_MODE_LEGACY)
                   setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
                }
            }
            "file" -> {
                val container = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                val fileTextView = TextView(this).apply {
                    text = "No file selected"
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                val selectButton = Button(this).apply {
                    text = "Select"
                    setOnClickListener {
                        currentFileFieldName = field.name
                        currentFileTextView = fileTextView
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
                        filePickerLauncher.launch(intent)
                    }
                }
                container.addView(fileTextView)
                container.addView(selectButton)
                return container
            }
            "date" -> {
                val layout = TextInputLayout(this)
                val editText = com.google.android.material.textfield.TextInputEditText(this).apply {
                    isFocusable = false
                    isClickable = true
                    setOnClickListener { showDatePicker(this, field.name) }
                }
                layout.addView(editText)
                return layout
            }
            "radio" -> {
                return RadioGroup(this).apply {
                    field.options?.forEach { option ->
                        addView(RadioButton(context).apply { text = option.label })
                    }
                    setOnCheckedChangeListener { group, checkedId ->
                        val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
                        val selectedOption = field.options?.find { it.label == checkedRadioButton.text }
                        fieldValues[field.name] = selectedOption?.value ?: ""
                        validateForm()
                    }
                }
            }
            "checkbox" -> {
                return if (field.options.isNullOrEmpty()) {
                    SwitchMaterial(this).apply {
                        text = field.label
                        setOnCheckedChangeListener { _, isChecked ->
                            fieldValues[field.name] = isChecked.toString()
                            validateForm()
                        }
                    }
                } else {
                    LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        field.options.forEach { option ->
                            addView(CheckBox(context).apply {
                                text = option.label
                                setOnCheckedChangeListener { _, _ ->
                                    val selected = (0 until childCount).map { getChildAt(it) as CheckBox }
                                        .filter { it.isChecked }
                                        .mapNotNull { checkbox -> field.options.find { it.label == checkbox.text }?.value }
                                        .joinToString(",")
                                    fieldValues[field.name] = selected
                                    validateForm()
                                }
                            })
                        }
                    }
                }
            }
            "dropdown",  "textarea" -> {
                val layout = TextInputLayout(this, null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_FilledBox_ExposedDropdownMenu)
                val autoComplete = AutoCompleteTextView(this).apply {
                    val options = field.options?.map { it.label } ?: emptyList()
                    setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, options))
                    setOnItemClickListener { _, _, position, _ ->
                        fieldValues[field.name] = field.options?.get(position)?.value ?: "" // MUDANÇA: de uuid para name
                        validateForm()
                    }
                }
                layout.addView(autoComplete)
                return layout
            }
            else -> return TextView(this).apply { text = "Not suported: ${field.type}" }
        }
    }

    private fun showDatePicker(editText: EditText, fieldName: String) {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build()
        picker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateString = sdf.format(Date(selection))
            editText.setText(dateString)
            fieldValues[fieldName] = dateString
            validateForm()
        }
        picker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun validateForm() {
        val allRequiredFilled = formModel.fields
            .filter { it.required == true }
            .all { field ->
                val value = fieldValues[field.name]
                !value.isNullOrEmpty() && value != "false"
            }
        if (allRequiredFilled) {
            saveButton?.setEnabledStyle()
        } else {
            saveButton?.setDisabledStyle()
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        }
        return fileName ?: uri.lastPathSegment ?: "File"
    }

}