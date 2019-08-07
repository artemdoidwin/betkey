package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class A2pWithdrawal (
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("parent")
    var parent: Any? = null,

    @SerializedName("event")
    var event: String? = null,

    @SerializedName("owner_type")
    var ownerType: Int? = null,

    @SerializedName("owner_id")
    var ownerId: String? = null,

    @SerializedName("status")
    var status: Boolean? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("errors")
    var errors: List<Any>? = null,

    @SerializedName("transaction")
    var transaction: Transaction? = null,

    @SerializedName("created")
    var created: Double? = null,

    @SerializedName("payment")
    var payment: Payment? = null
)