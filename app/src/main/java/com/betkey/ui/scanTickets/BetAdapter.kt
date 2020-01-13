package com.betkey.ui.scanTickets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.base.TranslationListener
import com.betkey.network.models.Event
import com.betkey.utils.Translation
import kotlinx.android.synthetic.main.item_bet_detail.view.*

class BetAdapter(private val list: MutableList<Event> = arrayListOf()) : RecyclerView.Adapter<BetAdapter.BetsViewHolder>(),
    TranslationListener {
    private lateinit var context: Context

    //translations
    private var home = ""
    private var draw = ""
    private var away = ""

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

    fun setItems(items: List<Event>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    inner class BetsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                "1" -> key = home
                "X" -> key = draw
                "2" -> key = away
            }
            return key
        }
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        home = dictionary[Translation.ScanTicketsDetails.HOME] ?: ""
        draw = dictionary[Translation.ScanTicketsDetails.DRAW] ?: ""
        away = dictionary[Translation.ScanTicketsDetails.AWAY] ?: ""
    }
}