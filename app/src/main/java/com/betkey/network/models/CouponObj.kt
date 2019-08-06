package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class CouponObj(
    @SerializedName("carryover_pool")
    var carryoverPool: Int? = null,

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("expires")
    var expires: String = ""

)