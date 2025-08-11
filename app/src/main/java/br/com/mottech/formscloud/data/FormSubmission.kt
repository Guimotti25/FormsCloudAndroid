package br.com.mottech.formscloud.data // Use o seu nome de pacote


import android.os.Parcelable // <-- IMPORT NECESSÁRIO
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize // <-- 1. ADICIONE ESTA ANOTAÇÃO
@Entity(tableName = "form_submissions")
@TypeConverters(MapTypeConverter::class)
data class FormSubmission(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val parentFormId: String,
    val fieldValues: Map<String, String>,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable // <-- 2. ADICIONE A IMPLEMENTAÇÃO DA INTERFACE

// A classe MapTypeConverter continua a mesma, sem alterações.
class MapTypeConverter {
    @TypeConverter
    fun fromString(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>): String {
        return Gson().toJson(map)
    }
}