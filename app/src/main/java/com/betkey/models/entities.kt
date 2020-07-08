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
    var stack: String = "",
    var totalOdds: String = "",
    var bonus: String = "",
    var potentialWin: String = "",
    var netWinning: String = "",
    var salesTax: String = "",
    var incomeTax: String = "",
    var salesTaxTitle: String = "",
    var incomeTaxTitle: String = "",
    var totalWin: String = ""

)

data class PayoutModel(
    var ticketNumber: String = "",
    var ticketCode: String = "",
    var date: String = "",
    var type: String = "",

    var place: String = "",
    var totalOdds: String = "",
    var stake: String = "",
    var currency: String = "",
    var salesTax: String = "",
    var potentialWin: String = "",
    var bonus: String = "",
    var totalWin: String = "",
    var incomeTax: String = "",
    var payout: String = ""
)