package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Market (
    @SerializedName("name")
    var name: String = "",

    @SerializedName("blocked")
    var blocked: Boolean = false,

    @SerializedName("lines")
    var lines: Map<String, Line>
)