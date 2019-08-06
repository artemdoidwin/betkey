package com.betkey.models

import java.io.Serializable

data class Product(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",

    val oldPrice: Double = 0.0,
    val price: Double = 0.0,
    val favorite: Boolean = false,
    val sale: Boolean = false,
    val height: Double = 0.0,
    val width: Double = 0.0,
    val length: Double = 0.0,
    val categoryId: Int = 0
) :  Serializable

data class Category(
    val id: Int = 0,
    val imageList: MutableList<String> = mutableListOf(),
    val name: String = ""
)

data class ModelItem (
    val name: String = "",
    val link: String = ""
)

data class BetDetail(
    var commandName: String,
    val betName: String
)