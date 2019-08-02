package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class OddsFormats (
    @SerializedName("dec")
    var dec: String = "",

    @SerializedName("us")
    var us: String = ""
)