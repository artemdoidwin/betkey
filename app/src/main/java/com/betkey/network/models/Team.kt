package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Team (
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String = ""
)