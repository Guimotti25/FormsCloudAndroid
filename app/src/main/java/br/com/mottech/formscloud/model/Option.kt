package br.com.mottech.formscloud.model // Use o seu nome de pacote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Option(
    @SerializedName("label")
    val label: String,

    @SerializedName("value")
    val value: String
) : Parcelable