package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Coupon (
    @SerializedName("coupon")
    var coupon: CouponObj? = null,

    @SerializedName("couponPool")
    var couponPool: Int = 0,

    @SerializedName("stakes")
    var stakes: List<String> = listOf(),

    @SerializedName("defaultStake")
    var defaultStake: Int = 0

)