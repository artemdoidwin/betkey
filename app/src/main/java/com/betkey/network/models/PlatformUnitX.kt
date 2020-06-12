package com.betkey.network.models

data class PlatformUnitX(
    val _id: String,
    val code: String,
    val created: Int,
    val img: Any,
    val name: String,
    val order: Int,
    val platform: PlatformX,
    val settings: Settings,
    val short_id: String,
    val status: Int,
    val updated: Int,
    val url: Any
)