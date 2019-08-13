package com.betkey.ui.scanTickets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.network.models.Event
import kotlinx.android.synthetic.main.item_bet_detail.view.*

class BetAdapter(private val list: List<Event>) : RecyclerView.Adapter<BetAdapter.BetsViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetsViewHolder {
        context = parent.context
        return BetsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_bet_detail, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BetsViewHolder, position: Int) {
        holder.updateItem(list[position], context)
    }

    class BetsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(event: Event, context: Context) {
            val refix = context.resources.getString(R.string.jackpot_game)
            val command1 = (event.teams["1"] ?: error("")).name
            val command2 = (event.teams["2"] ?: error("")).name
            val commandsName = "$refix ${adapterPosition + 1} - $command1 - $command2"
            itemView.item_bet_game_name.text = commandsName
            event.bet?.also { itemView.item_bet_comand_name.text = convertFieldToKey(it) }
        }

        private fun convertFieldToKey(field: String): String {
            var key = ""
            when (field) {
                "1" -> key = "Home"
                "X" -> key = "Draw"
                "2" -> key = "Away"
            }
            return key
        }
    }
}