package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class A2pDeposit (
    @SerializedName("id")
    var id: String = "",

    @SerializedName("parent")
    var parent: Any? = null,

    @SerializedName("event")
    var event: String = "",

    @SerializedName("owner_type")
    var ownerType: Int? = null,

    @SerializedName("owner_id")
    var ownerId: String = "",

    @SerializedName("status")
    var status: Boolean? = null,

    @SerializedName("message")
    var message: String = "",

    @SerializedName("errors")
    var errors: List<Any> = listOf(),

    @SerializedName("transaction")
    var transaction: Transaction? = null,

    @SerializedName("created")
    var created: Double = 0.0,

    @SerializedName("payment")
    var payment: Payment? = null
)