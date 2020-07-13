package com.betkey.network.models

data class StatisticDto(
    val baseData: BaseData,
    val cancelledSportBetsAmount: Double,
    val cancelledSportBetsCount: Int,
    val depositsCount: Int,
    val depositsTotalAmount: Double,
    val errors: List<Any>,
    val message: String,
    val payoutSportBetsAmount: Double,
    val payoutSportBetsCount: Int,
    val revertedSportBetsAmount: Double,
    val revertedSportBetsCount: Int,
    val soldJackpotBetsAmount: Double,
    val soldJackpotBetsCount: Int,
    val soldSportBetsAmount: Double,
    val soldSportBetsCount: Int,
    val status: Boolean,
    val wallet: Wallet,
    val withdrawalsCount: Int,
    val withdrawalsTotalAmount: Double
)