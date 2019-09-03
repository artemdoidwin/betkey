package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Confirm (
    @SerializedName("a2p_deposit")
    var a2pDeposit: A2pDeposit? = null
)