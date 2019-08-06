package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class ShopRestObject (
    @SerializedName("shop")
    val shop: Shop
) : MStatus()