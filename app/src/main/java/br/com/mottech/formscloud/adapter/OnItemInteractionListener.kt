package br.com.mottech.formscloud.adapter

import br.com.mottech.formscloud.data.FormSubmission

 interface OnItemInteractionListener {
    fun onItemClick(submission: FormSubmission)
    fun onItemLongClick(submission: FormSubmission)
}