package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class JackpotInfo (
    @SerializedName("events")
    var events: Map<String, Event>? = null,

    @SerializedName("altEvents")
//    var altEvents: AltEvents? = null,
    var altEvents: Map<String, Event>? = null,

    @SerializedName("coupon")
    var coupon: Coupon? = null
)