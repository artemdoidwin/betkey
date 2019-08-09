package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class AgentBettingResult (

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("message_data")
    var message_data: MessageData? = null,

    @SerializedName("error_code")
    var error_code: Int? = null,

    @SerializedName("error_message")
    var error_message: String? = null,

    @SerializedName("error_data")
    var error_data: List<String> = listOf(),

    @SerializedName("created")
    var created: Long? = null
)