package com.betkey.ui.sportbetting.marketsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.betkey.R
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.Market
import com.betkey.network.models.Team
import kotlinx.android.synthetic.main.item_details_sportbetting.view.*

class MarketsAdapter(
    private val basketList: MutableList<SportBetBasketModel>,
    private val mapMarkets: MutableMap<String, Market>,
    private val teams: Map<String, Team>,
    private val idEvent: String,
    private val league: String,
    private val date: String,
    private val gameListener: (SportBetBasketModel, MutableList<Int>) -> Unit
) :
    RecyclerView.Adapter<MarketsAdapter.GameViewHolder>() {
    private lateinit var recyclerView: RecyclerView
    private val list = mutableListOf<String>()
    private var listPosition = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_details_sportbetting, parent, false
                )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.updateItem(list[position])
    }

    fun setItems(
        entities: MutableList<String>,
        listPosition: MutableList<Int>
    ) {
        this.listPosition = listPosition
        list.clear()
        list.addAll(entities)
        notifyDataSetChanged()
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateItem(marketKey: String) {
            if (listPosition.contains(adapterPosition)) {
                itemView.bs_block_adapter.visibility = View.VISIBLE
                itemView.sp_expand.rotation = 180F
            }
            itemView.sp_expand.setOnClickListener {
                if (itemView.bs_block_adapter.visibility == View.VISIBLE) {
                    itemView.sp_expand.animate().rotation(0F).start()
                    itemView.bs_block_adapter.visibility = View.GONE
                    listPosition.remove(adapterPosition)
                } else {
                    itemView.sp_expand.animate().rotation(180F).start()
                    TransitionManager.beginDelayedTransition(recyclerView, ChangeBounds())
                    itemView.bs_block_adapter.visibility = View.VISIBLE
                    listPosition.add(adapterPosition)
                }
            }
            mapMarkets[marketKey]?.also {
                val namesLine = it.lines.keys.toMutableList()
                val adapter = LineAdapter(basketList, it.lines.toMutableMap(), teams, marketKey) { basketMod ->
                    gameListener(basketMod.copy(idEvent = idEvent, league = league, date = date, marketName = it.name), listPosition)
                }
                itemView.bs_block_adapter.adapter = adapter
                adapter.setItems(namesLine)

                itemView.name_market.text = it.name
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}