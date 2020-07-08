package com.betkey.network.models

data class Settings(
    val is_sales_tax_enabled: Boolean, //    val is_top_off_tax_enabled: Boolean,
    val mobile_iframe_src: String,
    val shop_iframe_src: String,
    val ticket_preview_support: Boolean,
    val ticket_preview_token: String,
    val ticket_preview_url: String,
    val sales_tax_value: Double, //    val top_off_tax_value: Double,
    val web_iframe_src: String
)