package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Rule (
    @SerializedName("type")
    var type: String = "",

    @SerializedName("market")
    var market: String = "",

    @SerializedName("bet")
    var bet: Double = 0.0,

    @SerializedName("number")
    var number: Int? = null,

    @SerializedName("betslip")
    var betslip: Double = 0.0,

    @SerializedName("bonus")
    var bonus: Int? = null
)