package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Instance(
    @SerializedName("id")
    var id: String = "",

    @SerializedName("name")
    var name: String = "",

    @SerializedName("short_name")
    var shortName: String = "",

    @SerializedName("country")
    var country: String = "",

    @SerializedName("currency")
    var currency: String = "",

    @SerializedName("min_stake")
    var minStake: Int? = null,

    @SerializedName("shops")
    var shops: List<ShopAgent> = listOf(),

    @SerializedName("languages")
    var languages: List<String> = listOf(),

    @SerializedName("default_language")
    var defaultLanguage: String = "",

    @SerializedName("timezone")
    var timezone: String = "",

    @SerializedName("bet_type")
    var betType: String = "",

    @SerializedName("betslip_bonuses")
    var betslipBonuses: List<BetslipBonus> = listOf(),

    @SerializedName("tax")
    var tax: Double? = null,

    @SerializedName("coupon_settings")
    var couponSettings: CouponSettings? = null
)