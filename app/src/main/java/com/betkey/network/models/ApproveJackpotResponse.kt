package com.betkey.network.models

data class ApproveJackpotResponse(
    val error_code: Int,
    val error_message: String,
    val message: String,
    val status: Int
)