package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Bet (
    @SerializedName("id")
    var id: String = "",

    @SerializedName("name")
    var name: String = "" ,

    @SerializedName("blocked")
    var blocked: Boolean = false,

    @SerializedName("odds")
    var odds: String = "",

    @SerializedName("odds_formats")
    var oddsFormats: OddsFormats? = null
)