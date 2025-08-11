package br.com.mottech.formscloud.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mottech.formscloud.data.FormSubmission
import br.com.mottech.formscloud.databinding.CardMyFormBinding
import java.text.SimpleDateFormat
import java.util.*

class SubmissionListAdapter(
    private var submissions: List<FormSubmission>,
    private val listener: OnItemInteractionListener
) : RecyclerView.Adapter<SubmissionListAdapter.SubmissionViewHolder>() {

    fun updateData(newSubmissions: List<FormSubmission>) {
        this.submissions = newSubmissions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionViewHolder {
        val binding = CardMyFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubmissionViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SubmissionViewHolder, position: Int) {
        holder.bind(submissions[position])
    }

    override fun getItemCount() = submissions.size

    class SubmissionViewHolder(private val binding: CardMyFormBinding,
                               private val listener: OnItemInteractionListener)
                               : RecyclerView.ViewHolder(binding.root) {
        fun bind(submission: FormSubmission) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.textViewDate.text = "Enviado em: ${sdf.format(Date(submission.createdAt))}"

            val firstName = submission.fieldValues["first_name"] ?: "N/A"
            val lastName = submission.fieldValues["last_name"] ?: ""
            binding.textViewPrimaryInfo.text = "$firstName $lastName"

            val email = submission.fieldValues["email"] ?: "Email não fornecido"
            binding.textViewSecondaryInfo.text = email

            binding.root.setOnClickListener {
                listener.onItemClick(submission)
            }

            binding.root.setOnLongClickListener {
                listener.onItemLongClick(submission)
                true
            }
        }
    }
}