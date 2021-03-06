package com.betkey.ui.sportbetting.eventsAdapters

import com.betkey.models.SportBetBasketModel

interface SportBettingListener {
    fun onCommandLeft(model: SportBetBasketModel)

    fun onIDraw(model: SportBetBasketModel)

    fun onCommandRight(model: SportBetBasketModel)

    fun onSetMarkets(eventId: String)
}