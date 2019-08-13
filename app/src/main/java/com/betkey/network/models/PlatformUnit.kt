package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class PlatformUnit (
    @SerializedName("_id")
    var id: String? = null,

    @SerializedName("short_id")
    var shortId: String? = null,

    @SerializedName("code")
    var code: String? = null,

    @SerializedName("name")
    var name: String? = null
)