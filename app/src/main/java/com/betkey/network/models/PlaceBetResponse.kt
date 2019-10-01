package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class PlaceBetResponse(
    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("message")
    var message: List<Event> = arrayListOf(),

    @SerializedName("error_code")
    var errorCode: Int? = null,

    @SerializedName("error_message")
    var errorMessage: String = ""

)