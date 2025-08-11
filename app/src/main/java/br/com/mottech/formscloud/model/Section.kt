package br.com.mottech.formscloud.model // Use o seu nome de pacote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Section(
    @SerializedName("title")
    val title: String,

    @SerializedName("from")
    val from: Int,

    @SerializedName("to")
    val to: Int,

    @SerializedName("index")
    val index: Int,

    @SerializedName("uuid")
    val uuid: String
) : Parcelable