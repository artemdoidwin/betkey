package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("_id")
    var id: String = "",

    @SerializedName("short_id")
    var shortId: String? = null,

    @SerializedName("method")
    var method: Int? = null,

    @SerializedName("agent")
    var agent: AgentShort? = null,

    @SerializedName("player")
    var player: PlayerShort? = null,

    @SerializedName("currency")
    var currency: String = "",

    @SerializedName("amount")
    var amount: String = "",

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("psp_id")
    var pspId: String? = null,

    @SerializedName("psp_payment_id")
    var psp_payment_id: String? = null,

    @SerializedName("psp_payment_code")
    var psp_payment_code: String? = null,

    @SerializedName("psp_name")
    var psp_name: String? = null,

    @SerializedName("psp_status")
    var psp_status: String? = null,

    @SerializedName("psp_created")
    var psp_created: String? = null,

    @SerializedName("psp_error_code")
    var psp_error_code: String? = null,

    @SerializedName("psp_error_message")
    var psp_error_message: String? = null,

    @SerializedName("created")
    var created: Long = 0,

    @SerializedName("updated")
    var updated: Long? = null,

    @SerializedName("timeline")
    var timeline: List<TimeLine>? = null
)