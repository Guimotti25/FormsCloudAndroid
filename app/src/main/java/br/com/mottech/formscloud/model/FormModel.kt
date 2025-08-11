package br.com.mottech.formscloud.model // Use o seu nome de pacote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormModel(
    @SerializedName("title")
    val title: String,

    @SerializedName("fields")
    val fields: List<Field>,

    @SerializedName("sections")
    val sections: List<Section>?
) : Parcelable