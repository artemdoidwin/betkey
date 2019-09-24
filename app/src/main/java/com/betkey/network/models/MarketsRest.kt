package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class MarketsRest (
    @SerializedName("markets")
    var markets: Map<String, Market> = mapOf()
)