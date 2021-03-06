package com.betkey.ui.sportbetting.marketsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.Line
import com.betkey.network.models.Team
import kotlinx.android.synthetic.main.item_sportbetting_line.view.*

class LineAdapter(
    private val basketList: MutableList<SportBetBasketModel>,
    private val mapLines: MutableMap<String, Line>,
    private val teams: Map<String, Team>,
    private val marketKey: String,
    private val clickListener: (SportBetBasketModel) -> Unit
) : RecyclerView.Adapter<LineAdapter.GameViewHolder>() {

    private val list =  mapLines.keys.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sportbetting_line, parent, false)
        )
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
                val adapter = BlockAdapter(basketList, it.bets.toMutableMap(), teams, marketKey) { basketMod ->
                    clickListener(basketMod.copy(lineName = name))
                }
                itemView.recycler_lines.adapter = adapter
            }
        }
    }
}