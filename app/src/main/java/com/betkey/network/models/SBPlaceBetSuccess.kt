package com.betkey.network.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class SBPlaceBetSuccess(
    @SerializedName("id")
    var id: String = "",

    @SerializedName("code")
    var code: String = "",

    @SerializedName("stake")
    var stake: Int? = null,

    @SerializedName("payout")
    var payout: String = "",

    @SerializedName("events")
    var events: List<Event>? = null,

    @SerializedName("created")
    var created: String = "",

    @SerializedName("updated")
    var updated: String = "",

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("live_status")
    var liveStatus: Boolean? = null,

    @SerializedName("win_status")
    var winStatus: Int? = null,

    @SerializedName("cancel_reason")
    var cancelReason: String = "",

    @SerializedName("total_odds")
    var totalOdds: Double? = null,

    @SerializedName("total_odds_formats")
    var totalOddsFormats: OddsFormats? = null,

    @SerializedName("tax")
    var tax: Double? = null,

    @SerializedName("gross_win")
    var grossWin: Double? = null,

    @SerializedName("approved")
    var approved: Boolean? = null,

    @SerializedName("instance_name")
    var instanceName: String = "",

    @SerializedName("campaign")
    var campaign: List<Any> = listOf(),

    @SerializedName("user_id")
    var userId: Int? = null,

    @SerializedName("shop_id")
    var shopId: Int? = null,

    @SerializedName("shop_name")
    var shopName: String = ""
) {
    companion object {
        fun checkStatus(statusBetslip: StatusBetslip): SBPlaceBetSuccess? {
            var placeBetSuccess: SBPlaceBetSuccess? = null
            when (statusBetslip.status) {
                0 -> placeBetSuccess
                1 -> placeBetSuccess = Gson().fromJson(
                    Gson().toJson(statusBetslip.message),
                    SBPlaceBetSuccess::class.java
                )
                2 -> placeBetSuccess
                6 -> placeBetSuccess
                else -> placeBetSuccess
            }
            return placeBetSuccess
        }
    }
}