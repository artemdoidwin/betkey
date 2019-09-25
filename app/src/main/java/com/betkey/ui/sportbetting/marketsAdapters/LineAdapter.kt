package com.betkey.ui.sportbetting.marketsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.network.models.Bet
import com.betkey.network.models.Line
import com.betkey.network.models.Team
import kotlinx.android.synthetic.main.item_sportbetting_line.view.*

class LineAdapter(
    val mapLines: MutableMap<String, Line>,
    private val teams: Map<String, Team>,
    private val marketName: String,
    private val clickListener: (Bet) -> Unit
) : RecyclerView.Adapter<LineAdapter.GameViewHolder>() {

    private  var selectedBet: Bet? = null
    private val list = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_sportbetting_line, parent, false
                )
        )
    }

    fun setItems(entities: MutableList<String>, bet: Bet?) {
        selectedBet = bet
        list.clear()
        list.addAll(entities)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position])
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(name: String) {
            if (mapLines.size > 1) {
                itemView.line_name.visibility = View.VISIBLE
                itemView.line_name.text = name
            } else {
                itemView.line_name.visibility = View.GONE
            }

            mapLines[name]?.also {
                val namesBets = it.bets.keys.toMutableList()
                val adapter = BlockAdapter(it.bets.toMutableMap(), teams, marketName) {bet ->
                    clickListener(bet)
                }
                itemView.recycler_lines.adapter = adapter
                adapter.setItems(namesBets, selectedBet)
            }
        }
    }
}