package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class AgentDeposit(
    @SerializedName("payment")
    var payment: PaymentRest? = null,

    @SerializedName("statuses")
    var statuses: Map<String, String>? = null,

    @SerializedName("methods")
    var methods: Map<String, String>? = null

) : MStatus()