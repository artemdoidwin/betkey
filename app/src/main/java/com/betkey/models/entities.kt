package com.betkey.models

import com.betkey.network.models.Bet

data class LotteryOrPickModel(
    var price: String = "",
    var round: String = "",
    var draw: String = "",
    var numbers: String = "",
    var winsCombinations: List<String> = listOf()
)

data class LotteryModel(
    var isSelected: Boolean,
    var number: Int
)

data class SportBetBasketModel(
    var idEvent: String = "",
    var league: String = "",
    var teamsName: String = "",
    var date: String = "",
    var marketKey: String = "",
    var marketName: String = "",
    var betWinName: String = "",
    var odds: String = "",
    var bet: Bet? = null,
    var betKey: String = "",
    var lineName: String = ""
)

data class PrintObj(
    var totalOdds: String = "",
    var bonus: String = "",
    var potentialWin: String = "",
    var potentialTax: String = "",
    var netWinning: String = ""
)