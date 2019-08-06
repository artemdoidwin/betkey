package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Line (
    @SerializedName("blocked")
    var blocked: Boolean = false,

    @SerializedName("bets")
    var bets: Map<String, Bet>
)