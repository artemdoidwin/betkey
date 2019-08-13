package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class TimeObj (
    @SerializedName("date")
    var date: String? = null,

    @SerializedName("timezone_type")
    var timezone_type: Int? = null,

    @SerializedName("timezone")
    var timezone: String? = null
)