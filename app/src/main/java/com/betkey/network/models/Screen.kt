package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Screen (
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("size")
    var size: String = ""
)