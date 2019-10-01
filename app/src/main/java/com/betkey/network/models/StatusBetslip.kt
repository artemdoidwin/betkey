package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class StatusBetslip(
    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("message")
    var message: Any? = null,

    @SerializedName("error_code")
    var error_code: Int? = null,

    @SerializedName("error_message")
    var error_message: String = ""
)