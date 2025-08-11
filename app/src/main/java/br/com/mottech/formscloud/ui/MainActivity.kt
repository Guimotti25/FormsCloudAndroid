package br.com.mottech.formscloud.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mottech.formscloud.adapter.FormListAdapter
import br.com.mottech.formscloud.databinding.ActivityMainBinding
import br.com.mottech.formscloud.model.FormModel
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val forms = loadFormsFromAssets()
        setupRecyclerView(forms)
    }

    private fun setupRecyclerView(forms: List<FormModel>) {
        binding.recyclerMain.layoutManager = LinearLayoutManager(this)
        binding.recyclerMain.adapter = FormListAdapter(forms) { selectedForm ->
            val intent = Intent(this, EntryListActivity::class.java).apply {
                putExtra("SELECTED_FORM", selectedForm)
            }
            startActivity(intent)
        }
    }

    private fun loadFormsFromAssets(): List<FormModel> {
        val gson = Gson()
        val formList = mutableListOf<FormModel>()

        val jsonFiles = listOf("all-fields.json", "200-form.json")

        jsonFiles.forEach { fileName ->
            try {
                val jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
                val form = gson.fromJson(jsonString, FormModel::class.java)
                formList.add(form)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return formList
    }
}