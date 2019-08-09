package com.betkey.ui.jackpot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.network.models.Event
import kotlinx.android.synthetic.main.item_game.view.*

class JackpotGamesAdapter(
    private val gameListener: GameListener
) : RecyclerView.Adapter<JackpotGamesAdapter.GameViewHolder>() {

    private val list = mutableListOf<Event>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        context = parent.context
        return GameViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_game, parent, false)
        )
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

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateItem(
            model: Event,
            context: Context,
            gameListener: GameListener
        ) {
            val refix = context.resources.getString(R.string.jackpot_game)
            val gameName = "$refix ${adapterPosition + 1} - ENGLAND - ${model.league!!.name}"
            val command1 = (model.teams["1"] ?: error("")).name
            val command2 = (model.teams["2"] ?: error("")).name
            val commandsName = "$command1 - $command2"
            itemView.item_winner_game_name.text = gameName
            itemView.item_winner_comand_name.text = commandsName

            if (adapterPosition % 2 == 0) itemView.item_winner_container.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            else itemView.item_winner_container.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.light_gray_color
                )
            )

            itemView.item_winner_command_left_btn.text = command1
            itemView.item_winner_command_left_btn.setOnClickListener {
                val bet = model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("1")
                bet?.also { gameListener.onCommandLeft("$refix $adapterPosition - $commandsName", it, "1") }
            }
            itemView.item_winner_draw_btn.setOnClickListener {
                val bet = model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("X")
                bet?.also {   gameListener.onIDraw( "$refix $adapterPosition - $commandsName", it, "X")}
            }
            itemView.item_winner_command_right_btn.text = command2
            itemView.item_winner_command_right_btn.setOnClickListener {
                val bet = model.markets["MRFT"]?.lines?.get("NA")?.bets?.get("2")
                bet?.also { gameListener.onCommandRight("$refix $adapterPosition - $commandsName", it, "2")}
            }
        }
    }
}