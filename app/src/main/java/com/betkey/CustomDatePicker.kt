package com.betkey

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_date_picker.view.*
import org.jetbrains.anko.layoutInflater
import java.text.SimpleDateFormat
import java.util.*

class CustomDatePicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){
    private val format = SimpleDateFormat("dd/MM/yy",Locale.getDefault())
    init {
        this.addView(context.layoutInflater.inflate(R.layout.custom_date_picker,null,false))
    }

    fun getDate()=  format.parse(dateText.text.toString())
}