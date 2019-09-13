package com.betkey.ui.lottery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.models.LotteryModel
import kotlinx.android.synthetic.main.item_lottery.view.*

class NumberAdapter(private val clickListener: (LotteryModel) -> Unit) :
    RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

    private val list = mutableListOf<LotteryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        return NumberViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_lottery, parent, false)
        )
    }

    fun setItems(entities: MutableList<LotteryModel>) {
        list.clear()
        list.addAll(entities)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.updateItem(list[position], clickListener)
    }

    class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(model: LotteryModel, clickListener: (LotteryModel) -> Unit) {
            itemView.item_lottery_btn.text = model.number.toString()
            if (model.isSelected){
                itemView.item_lottery_btn.background = ContextCompat.getDrawable(itemView.context,  R.drawable.button_yellow)
            }else{
                itemView.item_lottery_btn.background = ContextCompat.getDrawable(itemView.context,  R.drawable.button_gray)
            }

            itemView.item_lottery_btn.setOnClickListener { clickListener(model) }
        }
    }
}