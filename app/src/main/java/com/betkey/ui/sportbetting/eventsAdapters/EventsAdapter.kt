package com.betkey.ui.sportbetting.eventsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betkey.R
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.Event
import kotlinx.android.synthetic.main.item_sportbeting.view.*

class EventsAdapter(
    private val basketlist: MutableList<SportBetBasketModel>,
    private val gameListener: SportBettingListener,
    val mapGame: MutableMap<String, List<Event>>
) :
    RecyclerView.Adapter<EventsAdapter.GameViewHolder>() {

    private val list = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_sportbeting,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position], gameListener)
    }

    fun setItems(entities: MutableList<String>) {
        list.clear()
        list.addAll(entities)
        notifyDataSetChanged()
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateItem(name: String, gameListener: SportBettingListener) {
            val list = mapGame[name] as MutableList

           if (adapterPosition % 2 == 0) {
               itemView.item_cont.setBackgroundColor(
                   ContextCompat.getColor(itemView.context, R.color.light_gray_color)
               )
            } else {
                itemView.item_cont.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.white)
                )
            }

            itemView.league_name.text = name
            itemView.bs_events_adapter.adapter =
                LigueAdapter(basketlist, gameListener, list)
        }
    }
}