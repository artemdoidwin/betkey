package com.betkey.ui.jackpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import kotlinx.android.synthetic.main.item_bet_detail.view.*

class BetsAdapter (private val list: List<Pair<String, String>>)
    : RecyclerView.Adapter<BetsAdapter.BetsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetsViewHolder {
        return BetsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_bet_detail, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BetsViewHolder , position: Int) {
        holder.updateItem(list[position].first, list[position].second)
    }

    class BetsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(name: String, bet: String) {
            itemView.item_bet_game_name.text = name
            itemView.item_bet_comand_name.text = when(bet){
                "1"->"HOME"
                "X"->"DRAW"
                else -> "AWAY"
            }
        }
    }
}