package com.betkey.ui.sportbetting

interface SportBettingListener {
    fun onCommandLeft(commandName: String)

    fun onIDraw(commandName: String)

    fun onCommandRight(commandName: String)

    fun onSetMarkets(eventId: String)
}