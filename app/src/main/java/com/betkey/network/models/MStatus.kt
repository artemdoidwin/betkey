package com.betkey.network.models

import com.google.gson.annotations.SerializedName

open class MStatus (
    @SerializedName("status")
    var status: Boolean = false,

    @SerializedName("message")
    var message: String = "",

    @SerializedName("errors")
    var errors: List<ErrorObj> = arrayListOf()
)