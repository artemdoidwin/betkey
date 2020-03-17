package com.betkey

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_ticket_row.view.*
import org.jetbrains.anko.layoutInflater


class TicketRow@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

    init {
        this.addView(LayoutInflater.from(context).inflate(R.layout.custom_ticket_row,null,false))
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TicketRow,
            0, 0).apply {
        rowName.text =getString(R.styleable.TicketRow_name)
        rowValue.text = getString(R.styleable.TicketRow_value)
        }
    }

    fun setValue(text:Any){
        this.rowValue.text =text.toString()
    }
}