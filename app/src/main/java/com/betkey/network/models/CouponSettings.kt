package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class CouponSettings (
    @SerializedName("pool")
    var pool: Double? = null,

    @SerializedName("profit")
    var profit: Int? = null,

    @SerializedName("contingency")
    var contingency: Int? = null,

    @SerializedName("stakes")
    var stakes: String = "",

    @SerializedName("count_general_events")
    var countGeneralEvents: Int? = null,

    @SerializedName("count_alternative_events")
    var countAlternativeEvents: Int? = null,

    @SerializedName("min_won_bets")
    var minWonBets: Int? = null,


    @SerializedName("default_stake")
    var defaultStake:  Int? = null
)