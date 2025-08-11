package br.com.mottech.formscloud.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mottech.formscloud.databinding.CardItemFormBinding
import br.com.mottech.formscloud.model.FormModel

class FormListAdapter(
    private val forms: List<FormModel>,
    private val clickListener: (FormModel) -> Unit
) : RecyclerView.Adapter<FormListAdapter.FormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = CardItemFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val form = forms[position]
        holder.bind(form, clickListener)
    }

    override fun getItemCount() = forms.size

    class FormViewHolder(private val binding: CardItemFormBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(form: FormModel, clickListener: (FormModel) -> Unit) {
            binding.textViewFormTitle.text = form.title
            binding.root.setOnClickListener {
                clickListener(form)
            }
        }
    }
}