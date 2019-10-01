package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Shop(
    @SerializedName("_id")
    var id: String = "",

    @SerializedName("short_id")
    var shortId: String = "",

    @SerializedName("code")
    var code: String = "",

    @SerializedName("name")
    var name: String = ""
)