// No novo arquivo: PrimaryButton.kt

package br.com.mottech.formscloud.helper // Use o seu nome de pacote

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import br.com.mottech.formscloud.R

class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        setDisabledStyle()
    }

    fun setEnabledStyle() {
        background.mutate().setTint(ContextCompat.getColor(context, R.color.purple))
        setTextColor(ContextCompat.getColor(context, R.color.white))
        isEnabled = true
    }

    fun setDisabledStyle() {
        background.mutate().setTint(ContextCompat.getColor(context, R.color.gray))
        setTextColor(ContextCompat.getColor(context, R.color.black))
        isEnabled = false
    }
}