package com.betkey.ui.sportbetting.marketsAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.Bet
import com.betkey.network.models.Event
import com.betkey.network.models.Team
import com.betkey.ui.sportbetting.DetailsSportBitingFragment
import kotlinx.android.synthetic.main.item_detail_stake.view.*

class BlockAdapter(
    private val basketList: MutableList<SportBetBasketModel>,
    private val mapBets: MutableMap<String, Bet>,
    private val teams: Map<String, Team>,
    private val marketKey: String,
    private val clickListener: (SportBetBasketModel) -> Unit
) : RecyclerView.Adapter<BlockAdapter.GameViewHolder>() {

    private var list: MutableList<String> = mapBets.keys.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_detail_stake, parent, false
                )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position], clickListener)
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateItem(betKey: String, gameListener: (SportBetBasketModel) -> Unit) {
            mapBets[betKey]?.also { bet ->
                bet.id.toDoubleOrNull()?.also {
                    basketList.map {
                        if (it.bet == bet) {
                            itemView.market_btn.isChecked = true
                        }
                    }

                    var betWinName = ""
                    when (marketKey) {
                        "MRFT" -> {
                            when (betKey) {
                                "1" -> {
                                    val nameBtn = "${teams["1"]?.name}: ${bet.odds}"
                                    itemView.market_btn.text = nameBtn
                                    betWinName = teams["1"]?.name!!
                                }
                                "X" -> {
                                    val nameBtn = "X: ${bet.odds}"
                                    itemView.market_btn.text = nameBtn
                                    betWinName = bet.name
                                }
                                "2" -> {
                                    val nameBtn = "${teams["2"]?.name}: ${bet.odds}"
                                    itemView.market_btn.text = nameBtn
                                    betWinName = teams["2"]?.name!!
                                }
                            }
                        }
                        else -> {
                            val nameBtn = "${bet.name}: ${bet.odds}"
                            itemView.market_btn.text = nameBtn
                            betWinName = bet.name
                        }
                    }

                    itemView.market_btn.setOnClickListener {
                        val teamsName = "${teams["1"]?.name} - ${teams["2"]?.name}"
                        gameListener(
                            SportBetBasketModel(
                                teamsName = teamsName,
                                marketKey = marketKey,
                                betWinName = betWinName,
                                odds = bet.odds,
                                betKey = betKey,
                                bet = bet
                            )
                        )
                    }
                }
            }
            Log.d("TIMER", "${System.currentTimeMillis() - DetailsSportBitingFragment.time}")
        }
    }
}