package com.betkey.ui.jackpot

interface GameListener {

    fun onCommandLeft(product: String)

    fun onIDraw()

    fun onCommandRight(product: String)
}