package com.betkey.ui.jackpot

import com.betkey.network.models.Bet

interface GameListener {

    fun onCommandLeft(commandName: String, bet: Bet)

    fun onIDraw(commandName: String, bet: Bet)

    fun onCommandRight(commandName: String, bet: Bet)
}