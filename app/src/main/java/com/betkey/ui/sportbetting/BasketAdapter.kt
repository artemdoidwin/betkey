package com.betkey.ui.sportbetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.models.SportBetBasketModel
import kotlinx.android.synthetic.main.item_sportbetting_basket.view.*

class BasketAdapter(
    private val list: MutableList<SportBetBasketModel>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<BasketAdapter.GameViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sportbetting_basket, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position])
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateItem(model: SportBetBasketModel) {
            if (adapterPosition % 2 == 0) {
                itemView.basket_item_container.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.light_gray_color)
                )
            } else {
                itemView.basket_item_container.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.white)
                )
            }
            itemView.remove.setOnClickListener { clickListener(model.idEvent) }
            itemView.ligue.text = model.league
            itemView.command_name.text = model.teamsName
            itemView.date.text = model.date
            itemView.match_result.text = model.marketKey
            val commandWinName =
                "${itemView.context.resources.getString(R.string.sportbetting_bet)} ${model.betWinName}"
            itemView.bet_command_name.text = commandWinName
            val odds =
                "${itemView.context.resources.getString(R.string.sportbetting_odds)} ${model.odds}"
            itemView.odds.text = odds
        }
    }
}