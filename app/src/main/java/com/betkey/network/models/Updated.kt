package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Updated (
    @SerializedName("date")
    var date: String = "",

    @SerializedName("timezone_type")
    var timezone_type: Int = 0,

    @SerializedName("timezone")
    var timezone: String = ""
)