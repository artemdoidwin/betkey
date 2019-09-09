package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("_id")
    var id: String? = null,

    @SerializedName("player_id")
    var playerId: String = "",

    @SerializedName("payment_id")
    var paymentId: String = "",

    @SerializedName("transaction_id")
    var transactionId: Int = 0,

    @SerializedName("code")
    var code: Int = 0,

    @SerializedName("amount")
    var amount: Double = 0.0,

    @SerializedName("currency")
    var currency: String = "",

    @SerializedName("status")
    var status: Int = 0,

    @SerializedName("created")
    var created: Created? = null,

    @SerializedName("updated")
    var updated: Updated? = null

)