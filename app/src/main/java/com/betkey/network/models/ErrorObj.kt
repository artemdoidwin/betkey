package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class ErrorObj(
    @SerializedName("code")
    var code: Int? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("parameters")
    var parameters: Map<String, String>? = null

)