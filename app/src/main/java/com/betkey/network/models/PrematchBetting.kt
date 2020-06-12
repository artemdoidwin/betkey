package com.betkey.network.models

data class PrematchBetting(
    val errors: List<Any>,
    val message: String,
    val platform_unit: PlatformUnitX,
    val status: Boolean
)