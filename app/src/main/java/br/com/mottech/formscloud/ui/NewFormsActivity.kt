package br.com.mottech.formscloud.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import br.com.mottech.formscloud.data.AppDatabase
import br.com.mottech.formscloud.data.NewFormsViewModelFactory
import br.com.mottech.formscloud.databinding.ActivityNewFormsBinding
import br.com.mottech.formscloud.helper.PrimaryButton
import br.com.mottech.formscloud.model.Field
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class NewFormsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewFormsBinding
    private lateinit var viewModel: NewFormsViewModel
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private var currentFileTextView: TextView? = null
    private var currentFileFieldName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)
        val factory = NewFormsViewModelFactory(database)
        viewModel = ViewModelProvider(this, factory)[NewFormsViewModel::class.java]

        val form = getFormFromIntent() ?: return
        viewModel.setFormModel(form)
        title = "New - ${form.title}"

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    try {
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    currentFileFieldName?.let { fieldName ->
                        val fileName = getFileName(uri)
                        currentFileTextView?.text = fileName
                        viewModel.updateFieldValue(fieldName, uri.toString())
                        currentFileFieldName = null
                        currentFileTextView = null
                    }
                }
            }
        }

        observeViewModel()
        generateForm()
    }

    private fun getFormFromIntent(): br.com.mottech.formscloud.model.FormModel? {
        val form = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("FORM_TO_FILL", br.com.mottech.formscloud.model.FormModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("FORM_TO_FILL")
        }
        if (form == null) {
            finish()
            return null
        }
        return form
    }

    private fun observeViewModel() {
        viewModel.isFormValid.observe(this) { isValid ->
            val saveButton = binding.formContainer.findViewWithTag<PrimaryButton>("save_button")
            if (isValid) {
                saveButton?.setEnabledStyle()
            } else {
                saveButton?.setDisabledStyle()
            }
        }
    }

    private fun generateForm() {
        val container = binding.formContainer
        container.removeAllViews()

        val formModel = viewModel.formModel.value ?: return

        val sections = formModel.sections?.sortedBy { it.index }

        if (!sections.isNullOrEmpty()) {
            sections.forEach { section ->
                addHtmlSectionUsingWebView(section.title, container)
                if (section.from >= 0 && section.to < formModel.fields.size) {
                    val sectionFields = formModel.fields.subList(section.from, section.to + 1)
                    sectionFields.forEach { field ->
                        val isSingleCheckbox = field.type == "checkbox" && field.options.isNullOrEmpty()
                        if (field.type != "description" && !isSingleCheckbox) {
                            container.addView(createLabel(field))
                        }
                        container.addView(createInputView(field))
                    }
                }
            }
        } else {
            formModel.fields.forEach { field ->
                val isSingleCheckbox = field.type == "checkbox" && field.options.isNullOrEmpty()
                if (field.type != "description" && !isSingleCheckbox) {
                    container.addView(createLabel(field))
                }
                container.addView(createInputView(field))
            }
        }

        val button = PrimaryButton(this).apply {
            text = "Save"
            tag = "save_button"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dpToPx(48) }
            setOnClickListener {
                viewModel.saveSubmission {
                    finish()
                }
            }
        }
        container.addView(button)
    }

    private fun addHtmlSectionUsingWebView(html: String, container: ViewGroup) {
        val webView = WebView(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dpToPx(12)
        }
        webView.layoutParams = lp
        webView.settings.javaScriptEnabled = false
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.isVerticalScrollBarEnabled = false
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        container.addView(webView)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
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
            "text", "email", "number", "textarea" -> {
                val layout = TextInputLayout(this)
                val editText = com.google.android.material.textfield.TextInputEditText(this).apply {
                    when (field.type) {
                        "email" -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        "number" -> inputType = InputType.TYPE_CLASS_NUMBER
                        "textarea" -> {
                            minLines = 3
                            isSingleLine = false
                            gravity = android.view.Gravity.TOP
                        }
                    }
                    doOnTextChanged { text, _, _, _ ->
                        viewModel.updateFieldValue(field.name, text.toString())
                    }
                }
                layout.addView(editText)
                return layout
            }
            "password" -> {
                val layout = TextInputLayout(this).apply {
                    endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
                val editText = com.google.android.material.textfield.TextInputEditText(this).apply {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    doOnTextChanged { text, _, _, _ ->
                        viewModel.updateFieldValue(field.name, text.toString())
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
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
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
                        viewModel.updateFieldValue(field.name, selectedOption?.value ?: "")
                    }
                }
            }
            "checkbox" -> {
                return if (field.options.isNullOrEmpty()) {
                    SwitchMaterial(this).apply {
                        text = field.label
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.updateFieldValue(field.name, isChecked.toString())
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
                                    viewModel.updateFieldValue(field.name, selected)
                                }
                            })
                        }
                    }
                }
            }
            "dropdown" -> {
                val layout = TextInputLayout(this, null, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox_ExposedDropdownMenu)
                val autoComplete = AutoCompleteTextView(this).apply {
                    inputType = InputType.TYPE_NULL
                    val options = field.options?.map { it.label } ?: emptyList()
                    setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, options))
                    setOnItemClickListener { _, _, position, _ ->
                        viewModel.updateFieldValue(field.name, field.options?.get(position)?.value ?: "")
                    }
                    setOnClickListener {
                        showDropDown()
                    }
                }
                layout.addView(autoComplete)
                return layout
            }
            else -> return TextView(this).apply { text = "Not supported: ${field.type}" }
        }
    }

    private fun showDatePicker(editText: EditText, fieldName: String) {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build()
        picker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateString = sdf.format(Date(selection))
            editText.setText(dateString)
            viewModel.updateFieldValue(fieldName, dateString)
        }
        picker.show(supportFragmentManager, "DATE_PICKER")
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
