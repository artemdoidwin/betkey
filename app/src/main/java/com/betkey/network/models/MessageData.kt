package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class MessageData (
    @SerializedName("bet_code")
    var betCode: String = "",

    @SerializedName("stake")
    var stake: Int = 0,

    @SerializedName("coupon_id")
    var couponId: Int = 0
)