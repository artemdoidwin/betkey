package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Betkey(
    @SerializedName("_id")
    var id: String = "",

    @SerializedName("username")
    var username: String = "",

    @SerializedName("email")
    var email: String = "",

    @SerializedName("first_name")
    var firstName: String = "",

    @SerializedName("last_name")
    var lastName: String = "",

    @SerializedName("language")
    var language: String = "",

    @SerializedName("timezone")
    var timezone: String = "",

    @SerializedName("currency")
    var currency: String = "",

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("agent_id")
    var agentId: Int? = null,

    @SerializedName("created")
    var created: Long = 0,

    @SerializedName("updated")
    var updated: Long = 0,

    @SerializedName("credit_limit")
    var credit_limit: Int = 0,

    @SerializedName("wallets")
    var wallets: List<Wallet> = listOf()

)