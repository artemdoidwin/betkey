package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class BetLookupObj (
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("stake")
    var stake: Int? = null,
    @SerializedName("events")
    var events: List<Event>? = listOf(),
    @SerializedName("created")
    var created: Created? = null,
    @SerializedName("updated")
    var updated: Updated? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("live_status")
    var liveStatus: Boolean? = null,
    @SerializedName("win_status")
    var winStatus: Int? = null,
    @SerializedName("tax")
    var tax: Int? = null,
    @SerializedName("approved")
    var approved: Boolean? = null,
    @SerializedName("instance")
    var instance: Any? = null,
    @SerializedName("payoutInfo")
    var payoutInfo: Any? = null,
    @SerializedName("totalWin")
    var totalWin: Int? = null,
    @SerializedName("couponPayoutAmounts")
    var couponPayoutAmounts: Any? = null

)
