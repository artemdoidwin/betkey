package com.betkey.ui.sportbetting.eventsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.network.models.Event
import com.betkey.ui.sportbetting.SportBettingListener
import com.betkey.utils.dateToString3
import com.betkey.utils.toFullDate
import kotlinx.android.synthetic.main.item_sportbeting_ligue.view.*

class LigueAdapter(
    private val gameListener: SportBettingListener,
    val list: MutableList<Event>
) :
    RecyclerView.Adapter<LigueAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_sportbeting_ligue,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position], gameListener)
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(model: Event, gameListener: SportBettingListener) {
            model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("1")?.also { bet ->
                val btnText = "1: ${bet.odds}"
                itemView.item_winner_command_left_btn.text = btnText
                itemView.item_winner_command_left_btn.setOnClickListener {
                    gameListener.onCommandLeft(bet.name)
                }
            }
            model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("X")?.also { bet ->
                val btnText = "X: ${bet.odds}"
                itemView.item_winner_draw_btn.text = btnText
                itemView.item_winner_draw_btn.setOnClickListener {
                    gameListener.onIDraw(bet.name)
                }
            }
            model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("2")?.also { bet ->
                val btnText = "2: ${bet.odds}"
                itemView.item_winner_command_right_btn.text = btnText
                itemView.item_winner_command_right_btn.setOnClickListener {
                    gameListener.onCommandRight(bet.name)
                }
            }

            itemView.item_sport_betting_date.text = model.startTime!!.toFullDate().dateToString3()
            val nameCommand = "${model.teams["1"]?.name} - ${model.teams["2"]?.name}"
            itemView.item_sport_betting_command_name.text = nameCommand

            val odds = "+${model.marketsCount.toString()}"
            itemView.details.text = odds
            itemView.details.setOnClickListener { gameListener.onSetMarkets(model.id!!) }
        }
    }
}