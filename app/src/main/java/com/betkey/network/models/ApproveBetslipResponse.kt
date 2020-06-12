package com.betkey.network.models

data class ApproveBetslipResponse(
    val error_code: Int,
    val error_message: String,
    val message: Message,
    val status: Int
)