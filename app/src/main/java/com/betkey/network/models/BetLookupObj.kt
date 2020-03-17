package com.betkey.network.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.lang.Exception

data class DummyBetLookupObj(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("code")
    var code: String = "",
    @SerializedName("stake")
    var stake: Double? = null,
    @SerializedName("events")
    var events: List<Event>? = listOf(),
    @SerializedName("created")
    var created: String? = null,
    @SerializedName("updated")
    var updated: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("live_status")
    var liveStatus: Boolean? = null,
    @SerializedName("win_status")
    var winStatus: Int? = null,
    @SerializedName("tax")
    var tax: Double? = null,
    @SerializedName("approved")
    var approved: Boolean? = null,
    @SerializedName("instance")
    var instance: Instance? = null,
    @SerializedName("payoutInfo")
    var payoutInfo: Any? = null,
    @SerializedName("totalWin")
    var totalWin: Int? = null,
    @SerializedName("couponPayoutAmounts")
    var couponPayoutAmounts: Any? = null,
    @SerializedName("payout")
    var payout: String = "",
    @SerializedName("cancel_reason")
    var cancelReason: String = "",
    @SerializedName("total_odds")
    var totalOdds: Double? = null,
    @SerializedName("total_odds_formats")
    var totalOddsFormats: OddsFormats? = null,
    @SerializedName("gross_win")
    var grossWin: Double? = null,
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
)

data class BetLookupObj(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("code")
    var code: String = "",
    @SerializedName("stake")
    var stake: Double? = null,
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
    var tax: Double? = null,
    @SerializedName("approved")
    var approved: Boolean? = null,
    @SerializedName("instance")
    var instance: Instance? = null,
    @SerializedName("payoutInfo")
    var payoutInfo: Any? = null,
    @SerializedName("totalWin")
    var totalWin: Int? = null,
    @SerializedName("couponPayoutAmounts")
    var couponPayoutAmounts: Any? = null,
    @SerializedName("payout")
    var payout: String = "",
    @SerializedName("cancel_reason")
    var cancelReason: String = "",
    @SerializedName("total_odds")
    var totalOdds: Double? = null,
    @SerializedName("total_odds_formats")
    var totalOddsFormats: OddsFormats? = null,
    @SerializedName("gross_win")
    var grossWin: Double? = null,
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
        fun checkStatus(statusBetslip: StatusBetslip): BetLookupObj? {
            var placeBetSuccess: BetLookupObj? = null
            when (statusBetslip.status) {
                0 -> placeBetSuccess
                1 -> placeBetSuccess = parse(statusBetslip.message)
                2 -> placeBetSuccess
                6 -> placeBetSuccess
                else -> placeBetSuccess
            }
            return placeBetSuccess
        }

        fun parse(jsonObj: Any?): BetLookupObj? {
            var obj: BetLookupObj? = null

            try {
                obj = Gson().fromJson(
                    Gson().toJson(jsonObj),
                    BetLookupObj::class.java)

            } catch (e : Exception) {

                val dummy = Gson().fromJson(
                    Gson().toJson(jsonObj),
                    DummyBetLookupObj::class.java)
                obj = toBetLookup(dummy)
            } finally {
                return obj
            }
        }

        fun toBetLookup(dummy: DummyBetLookupObj) : BetLookupObj {
            return  BetLookupObj(
                dummy.id,
                dummy.code,
                dummy.stake,
                dummy.events,
                Created(dummy.created!!, 0, ""),
                Updated(dummy.updated!!, 0, ""),
                dummy.status,
                dummy.liveStatus,
                dummy.winStatus,
                dummy.tax,
                dummy.approved,
                dummy.instance,
                dummy.payoutInfo,
                dummy.totalWin,
                dummy.couponPayoutAmounts,
                dummy.payout,
                dummy.cancelReason,
                dummy.totalOdds,
                dummy.totalOddsFormats,
                dummy.grossWin,
                dummy.instanceName,
                dummy.campaign,
                dummy.userId,
                dummy.shopId,
                dummy.shopName
            )
        }
    }
}



data class BetLookupObj2(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("code")
    var code: String = "",
    @SerializedName("stake")
    var stake: Double? = null,
    @SerializedName("events")
    var events: List<Event>? = listOf(),
    @SerializedName("created")
    var created: String? = null,
    @SerializedName("updated")
    var updated: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("live_status")
    var liveStatus: Boolean? = null,
    @SerializedName("win_status")
    var winStatus: Int? = null,
    @SerializedName("tax")
    var tax: Double? = null,
    @SerializedName("approved")
    var approved: Boolean? = null,
    @SerializedName("instance")
    var instance: Instance? = null,
    @SerializedName("payoutInfo")
    var payoutInfo: Any? = null,
    @SerializedName("totalWin")
    var totalWin: Int? = null,
    @SerializedName("couponPayoutAmounts")
    var couponPayoutAmounts: Any? = null,
    @SerializedName("payout")
    var payout: String = "",
    @SerializedName("cancel_reason")
    var cancelReason: String = "",
    @SerializedName("total_odds")
    var totalOdds: Double? = null,
    @SerializedName("total_odds_formats")
    var totalOddsFormats: OddsFormats? = null,
    @SerializedName("gross_win")
    var grossWin: Double? = null,
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
)
