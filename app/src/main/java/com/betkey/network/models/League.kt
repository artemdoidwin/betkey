package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class League (
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String = "" ,

    @SerializedName("order")
    var order: Int = 0
)