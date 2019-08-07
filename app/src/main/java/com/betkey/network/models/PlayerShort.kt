package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class PlayerShort (

    @SerializedName("_id")
    var id: String = "",

    @SerializedName("short_id")
    var shortId: Int = 0,

    @SerializedName("username")
    var userName: String? = null,

    @SerializedName("phone")
    var phone: String? = null,

    @SerializedName("email")
    var email: String? = null
)