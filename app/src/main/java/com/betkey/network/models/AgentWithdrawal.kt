package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class AgentWithdrawal (
    @SerializedName("confirm")
    val confirm: Confirm? = null,

    @SerializedName("statuses")
    val statuses: Map<Int, String>? = null,

    @SerializedName("methods")
    val methods: Map<Int, String>? = null
): MStatus()