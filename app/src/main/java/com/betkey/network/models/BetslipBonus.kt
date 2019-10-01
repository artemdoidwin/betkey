package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class BetslipBonus (
    @SerializedName("name")
    var name: String = "",

    @SerializedName("rules")
    var rules: List<Rule> =  listOf()
)