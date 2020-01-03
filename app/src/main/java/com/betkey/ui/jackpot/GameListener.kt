package com.betkey.ui.jackpot

import com.betkey.network.models.Bet

interface GameListener {

    fun onCommandLeft(commandName: String, bet: Bet, selection: String, isAlternativeEvent: Boolean = false)

    fun onIDraw(commandName: String, bet: Bet, selection: String, isAlternativeEvent: Boolean = false)

    fun onCommandRight(commandName: String, bet: Bet, selection: String, isAlternativeEvent: Boolean = false)
}