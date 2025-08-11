package br.com.mottech.formscloud.model // Use o seu nome de pacote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Field(
    @SerializedName("type")
    val type: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("required")
    val required: Boolean?,

    @SerializedName("options")
    val options: List<Option>?,

    @SerializedName("uuid")
    val uuid: String
) : Parcelable