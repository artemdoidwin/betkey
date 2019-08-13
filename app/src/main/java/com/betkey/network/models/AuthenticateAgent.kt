package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class AuthenticateAgent(
    @SerializedName("token")
    var token: String = "",

    @SerializedName("token_expires")
    var tokenExpires: Long = 0,

    @SerializedName("agent")
    var agent: Agent? = null,

    @SerializedName("wallets")
    var wallets: List<Wallet>? = null
) : MStatus()