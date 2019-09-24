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

data class LotteryOrPickModel(
    var price: String = "",
    var round: String = "",
    var draw: String = "",
    var numbers: String = "",
    var winsCombinations: List<String> = listOf()
)



data class LotteryModel(
    var isSelected: Boolean,
    var number: Int
)


data class DetailsSportBetModel(
    var com1: String,
    var x: String,
    var com2: String
)