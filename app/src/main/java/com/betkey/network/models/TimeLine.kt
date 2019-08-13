package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class TimeLine (

    @SerializedName("action")
    var action: String = "",

    @SerializedName("time")
    var time: Long = 0   ,

    @SerializedName("transaction_id")
    var transactionId: Int = 0
)