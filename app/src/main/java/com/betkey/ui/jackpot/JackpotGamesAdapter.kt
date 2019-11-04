package com.betkey.ui.jackpot

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.network.models.Event
import kotlinx.android.synthetic.main.item_game.view.*

class JackpotGamesAdapter(private val gameListener: GameListener) :
    RecyclerView.Adapter<JackpotGamesAdapter.GameViewHolder>() {

    private val list = mutableListOf<Event>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        context = parent.context
        return GameViewHolder(LayoutInflater.from(context).inflate(R.layout.item_game, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position], context, gameListener)
    }

    fun setItems(entities: MutableList<Event>) {
        list.clear()
        list.addAll(entities)
        notifyDataSetChanged()
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateItem(model: Event, context: Context, gameListener: GameListener) {
            val prefix = context.resources.getString(R.string.jackpot_game)

            val gameName = if (model.isAltGame) {
               context.resources.getString(R.string.jackpot_alternative_game)
            } else {
                "$prefix ${adapterPosition + 1} - ENGLAND - ${model.league!!.name}"
            }

            val command1 = (model.teams["1"] ?: error("")).name
            val command2 = (model.teams["2"] ?: error("")).name
            val commandsName = "$command1 - $command2"
            itemView.item_winner_game_name.text = gameName
            itemView.item_winner_comand_name.text = commandsName

            if (adapterPosition % 2 == 0) {
                itemView.item_winner_container.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                itemView.item_winner_container.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_gray_color
                    )
                )
            }

            val radioBtnIds = mutableListOf(R.id.item_winner_command_left_btn, R.id.item_winner_draw_btn, R.id.item_winner_command_right_btn)
            if (model.btnChecked in radioBtnIds.indices){
                itemView.jackpot_items.check(radioBtnIds[model.btnChecked])
            } else {
                itemView.jackpot_items.clearCheck()
            }

            Log.e(" qwert", "Model 123456789  ${model.btnChecked == 1}")

            itemView.item_winner_command_left_btn.text = command1
            itemView.item_winner_command_left_btn.setOnClickListener {
                val bet = model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("1")
                bet?.also { gameListener.onCommandLeft("$prefix ${adapterPosition + 1} - $commandsName", it, "1") }
                list[adapterPosition].btnChecked = 0
            }

            itemView.item_winner_draw_btn.setOnClickListener {
                val bet = model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("X")
                bet?.also { gameListener.onIDraw("$prefix ${adapterPosition + 1} - $commandsName", it, "X") }
                list[adapterPosition].btnChecked = 1
            }

            itemView.item_winner_command_right_btn.text = command2
            itemView.item_winner_command_right_btn.setOnClickListener {
                val bet = model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("2")
                bet?.also { gameListener.onCommandRight("$prefix ${adapterPosition + 1} - $commandsName", it, "2") }
                list[adapterPosition].btnChecked = 2
            }

            Log.d(
                "qwert",
                "Model ${model.market_name} position $adapterPosition model ${model.btnChecked} ${itemView.item_winner_command_left_btn.isChecked} ${itemView.item_winner_draw_btn.isChecked} ${itemView.item_winner_command_right_btn.isChecked}"
            )
        }
    }
}