package com.betkey.ui.jackpot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import kotlinx.android.synthetic.main.item_game.view.*
import java.util.*

class JackpotGamesAdapter(
    private val gameListener: GameListener
) : RecyclerView.Adapter<JackpotGamesAdapter.GameViewHolder>() {

    private val list = ArrayList<String>()
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

    fun setItems(entities: MutableList<String>) {
        list.clear()
        list.addAll(entities)
        notifyDataSetChanged()
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateItem(
            model: String,
            context: Context,
            gameListener: GameListener
        ) {
            adapterPosition
            val gameName = "${model} $"
            val commandName = "${model} $"
            itemView.item_winner_game_name.text = gameName
            itemView.item_winner_comand_name.text = commandName

            if (adapterPosition % 2 == 0)    itemView.item_winner_container.setBackgroundColor( ContextCompat.getColor(context,  R.color.white))
            else itemView.item_winner_container.setBackgroundColor( ContextCompat.getColor(context,  R.color.light_gray_color))

            itemView.item_winner_command_left_btn.setOnClickListener {
                gameListener.onCommandLeft(model)
            }
            itemView.item_winner_draw_btn.setOnClickListener {
                gameListener.onIDraw()
            }
            itemView.item_winner_command_right_btn.setOnClickListener {
                gameListener.onCommandRight(model)
            }
        }
    }
}