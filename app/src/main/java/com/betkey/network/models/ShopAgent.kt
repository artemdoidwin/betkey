package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class ShopAgent(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("instance")
    var instance: Instance? = null,

    @SerializedName("machines")
    var machines: List<Machine> = listOf(),

    @SerializedName("screens")
    var screens: List<Screen> = listOf(),

    @SerializedName("balance")
    var balance: Int? = null,

    @SerializedName("limit")
    var limit: Int? = null,

    @SerializedName("logo_url")
    var logo_url: String = ""
)