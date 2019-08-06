package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class PlayerRestObject(
    @SerializedName("wallets")
    val player: Player? = null
) : MStatus()