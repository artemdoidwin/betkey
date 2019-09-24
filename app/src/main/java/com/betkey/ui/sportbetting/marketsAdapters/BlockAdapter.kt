package com.betkey.ui.sportbetting.marketsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.network.models.Bet
import com.betkey.network.models.Team
import kotlinx.android.synthetic.main.item_detail_stake.view.*

class BlockAdapter(
    val mapBets: MutableMap<String, Bet>,
    private val teams: Map<String, Team>,
    private val marketName: String,
    private val clickListener: (Bet) -> Unit
) : RecyclerView.Adapter<BlockAdapter.GameViewHolder>() {

    private val list = mutableListOf<String>()
    private var selectedBet: Bet? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_detail_stake, parent, false
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
        holder.updateItem(list[position], clickListener)
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(name: String, gameListener: (Bet) -> Unit) {
            mapBets[name]?.also { bet ->
                selectedBet?.also { sBet ->
                    if (sBet == bet) {
                        itemView.market_btn.isChecked = true
                    }
                }
                itemView.market_btn.setOnClickListener { gameListener(bet) }
                when (marketName) {
                    "MRFT" -> {
                        when (name) {
                            "1" -> {
                                val nameBtn = "${teams["1"]?.name}: ${bet.odds}"
                                itemView.market_btn.text = nameBtn
                            }
                            "X" -> {
                                val nameBtn = "X: ${bet.odds}"
                                itemView.market_btn.text = nameBtn
                            }
                            "2" -> {
                                val nameBtn = "${teams["2"]?.name}: ${bet.odds}"
                                itemView.market_btn.text = nameBtn
                            }
                        }
                    }
                    else -> {
                        val nameBtn = "${bet.name}: ${bet.odds}"
                        itemView.market_btn.text = nameBtn
                    }
                }
            }
        }
    }
}