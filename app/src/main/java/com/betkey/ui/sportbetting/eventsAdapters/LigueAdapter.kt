package com.betkey.ui.sportbetting.eventsAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.Event
import com.betkey.ui.sportbetting.SportBettingListener
import com.betkey.utils.dateToString3
import com.betkey.utils.toFullDate
import kotlinx.android.synthetic.main.item_sportbeting_ligue.view.*

class LigueAdapter(
    private val basketlist: MutableList<SportBetBasketModel>,
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
            checkBetId(model)?.also {
                val basketModel = SportBetBasketModel()
                val leagueName = "${model.league?.name}"
                val teamsName = "${model.teams["1"]?.name} - ${model.teams["2"]?.name}"
                val date = model.startTime!!.toFullDate().dateToString3()
                val marketKey = model.markets.keys.toList()[0]
                var marketName = ""
                model.markets[marketKey]?.name?.also {
                    marketName = it
                }

                model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("X")?.name?.also { drawName ->
                    model.teams["1"]?.name?.also { firstTeamName ->
                        model.teams["2"]?.name?.also { secondTeamName ->
                            basketlist.map {
                                if (it.idEvent == model.id) {
                                    when (it.betWinName) {
                                        firstTeamName -> itemView.item_winner_command_left_btn.isChecked =
                                            true
                                        drawName -> itemView.item_winner_draw_btn.isChecked = true
                                        secondTeamName -> itemView.item_winner_command_right_btn.isChecked =
                                            true
                                    }
                                }
                            }
                        }
                    }
                }
                model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("1")?.also { bet ->
                    val btnText = "1: ${bet.odds}"
                    itemView.item_winner_command_left_btn.text = btnText

                    itemView.item_winner_command_left_btn.setOnClickListener {
                        model.teams["1"]?.name?.also { betWinName ->
                            model.id?.also { idEvent ->
                                gameListener.onCommandLeft(
                                    basketModel.copy(
                                        idEvent = idEvent,
                                        league = leagueName,
                                        teamsName = teamsName,
                                        date = date,
                                        marketKey = marketKey,
                                        marketName = marketName,
                                        betWinName = betWinName,
                                        odds = bet.odds,
                                        bet = bet.copy(id = bet.id.toDouble().toInt().toString()),
                                        betKey = "1",
                                        lineName = "NA"
                                    )
                                )
                            }
                        }
                    }
                }
                model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("X")?.also { bet ->
                    val btnText = "X: ${bet.odds}"
                    itemView.item_winner_draw_btn.text = btnText

                    itemView.item_winner_draw_btn.setOnClickListener {
                        model.id?.also { idEvent ->
                            gameListener.onIDraw(
                                basketModel.copy(
                                    idEvent = idEvent,
                                    league = leagueName,
                                    teamsName = teamsName,
                                    date = date,
                                    marketKey = marketKey,
                                    marketName = marketName,
                                    betWinName = bet.name,
                                    odds = bet.odds,
                                    bet = bet.copy(id = bet.id.toDouble().toInt().toString()),
                                    betKey = "X",
                                    lineName = "NA"
                                )
                            )
                        }
                    }
                }
                model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("2")?.also { bet ->
                    val btnText = "2: ${bet.odds}"
                    itemView.item_winner_command_right_btn.text = btnText

                    itemView.item_winner_command_right_btn.setOnClickListener {
                        model.teams["2"]?.name?.also { betWinName ->
                            model.id?.also { idEvent ->
                                gameListener.onCommandRight(
                                    basketModel.copy(
                                        idEvent = idEvent,
                                        league = leagueName,
                                        teamsName = teamsName,
                                        date = date,
                                        marketKey = marketKey,
                                        marketName = marketName,
                                        betWinName = betWinName,
                                        odds = bet.odds,
                                        bet = bet.copy(id = bet.id.toDouble().toInt().toString()),
                                        betKey = "2",
                                        lineName = "NA"
                                    )
                                )
                            }
                        }
                    }
                }

                itemView.item_sport_betting_date.text = date
                val nameCommand = "${model.teams["1"]?.name} - ${model.teams["2"]?.name}"
                itemView.item_sport_betting_command_name.text = nameCommand

                val odds = "+${model.marketsCount.toString()}"
                itemView.details.text = odds
                itemView.details.setOnClickListener { gameListener.onSetMarkets(model.id!!) }
            }
        }
    }

    private fun checkBetId(model: Event): Double? {
        var newId: Double? = null
        model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("1")?.also { bet ->
            model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("X")?.also { betX ->
                model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("2")?.also { bet2 ->
                    newId = bet.id.toDoubleOrNull()
                }
            }
        }
        return newId
    }
}