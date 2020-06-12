package com.betkey.network.models

data class ApproveBetslipResponse(
    val error_code: Int,
    val error_message: String,
    val message: Any,
    val status: Int
)