package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class PaymentRest (

    @SerializedName("a2p_withdrawal")
    var a2p_withdrawal: A2pWithdrawal? = null,

    @SerializedName("player_deposit")
    var player_deposit: PlayerDeposit? = null

)