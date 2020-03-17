package com.betkey.network.models

data class StatisticDto(
    val baseData: BaseData,
    val cancelledSportBetsAmount: Int,
    val cancelledSportBetsCount: Int,
    val depositsCount: Int,
    val depositsTotalAmount: Int,
    val errors: List<Any>,
    val message: String,
    val payoutSportBetsAmount: Int,
    val payoutSportBetsCount: Int,
    val revertedSportBetsAmount: Int,
    val revertedSportBetsCount: Int,
    val soldJackpotBetsAmount: Int,
    val soldJackpotBetsCount: Int,
    val soldSportBetsAmount: Int,
    val soldSportBetsCount: Int,
    val status: Boolean,
    val wallet: Wallet,
    val withdrawalsCount: Int,
    val withdrawalsTotalAmount: Int
)