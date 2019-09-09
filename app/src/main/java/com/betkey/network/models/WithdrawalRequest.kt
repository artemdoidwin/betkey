package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class WithdrawalRequest(
    @SerializedName("request")
    var request: Request? = null,

    @SerializedName("player")
    var player: Player? = null,

    @SerializedName("type")
    var type: Int = 0
):MStatus()