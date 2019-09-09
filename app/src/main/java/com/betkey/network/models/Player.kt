package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("_id")
    var id: String? = null,

    @SerializedName("phone")
    var phone: String = "",

    @SerializedName("currency")
    var currency: String = "",

    @SerializedName("country")
    var country: String = "",

    @SerializedName("timezone")
    var timezone: String = "",

    @SerializedName("status")
    var status: Int = 0,

    @SerializedName("referral")
    var referral: String = "",

    @SerializedName("player_id")
    var playerId: Int = 0,

    @SerializedName("created")
    var created: Long = 0,

    @SerializedName("updated")
    var updated: Long = 0,

    @SerializedName("phones")
    var phonesList: List<String> = listOf(),

    @SerializedName("first_name")
    var first_name: String = "",

    @SerializedName("last_name")
    var last_name: String = "",

    @SerializedName("birthday")
    var birthday: String = "",

    @SerializedName("gender")
    var gender: Int? = null,

    @SerializedName("language")
    var language: String = "",

    @SerializedName("wallets")
    var wallets: List<Wallet> = listOf()
)