package br.com.mottech.formscloud // Use o seu nome de pacote

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mottech.formscloud.databinding.CardFormDetailsBinding
import br.com.mottech.formscloud.databinding.CardImageDetailBinding
import br.com.mottech.formscloud.model.Field
import com.bumptech.glide.Glide

class SubmissionDetailAdapter(
    fields: List<Field>,
    private val answers: Map<String, String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val displayableFields = fields.filter {
        it.type != "description" && it.name != "terms"
    }

    companion object {
        private const val VIEW_TYPE_TEXT = 1
        private const val VIEW_TYPE_IMAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayableFields[position].type) {
            "file" -> VIEW_TYPE_IMAGE
            else -> VIEW_TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val binding = CardImageDetailBinding.inflate(inflater, parent, false)
                ImageDetailViewHolder(binding)
            }
            else -> {
                val binding = CardFormDetailsBinding.inflate(inflater, parent, false)
                TextDetailViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val field = displayableFields[position]
        val answer = answers[field.name] ?: "Blank"

        when (holder) {
            is ImageDetailViewHolder -> holder.bind(field, answer)
            is TextDetailViewHolder -> holder.bind(field, answer)
        }
    }

    override fun getItemCount() = displayableFields.size

    class TextDetailViewHolder(private val binding: CardFormDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(field: Field, answer: String) {
            binding.textViewQuestionLabel.text = field.label
            binding.textViewAnswer.text = answer
        }
    }

    class ImageDetailViewHolder(private val binding: CardImageDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(field: Field, answerUriString: String) {
            binding.textViewQuestionLabel.text = field.label

            Glide.with(itemView.context)
                .load(Uri.parse(answerUriString))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imageViewAnswer)
        }
    }
}