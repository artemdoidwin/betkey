package com.betkey.models

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
    val idEvent: String = "",
    var league: String = "",
    var teamsName: String = "",
    var date: String = "",
    var marketName: String = "",
    var betWinName: String = "",
    var odds: String = ""
)

data class SportBetParamsModel(
    var marketKey: String = "",
    var lineKey: String = "",
    val totalOdds: String = ""
)