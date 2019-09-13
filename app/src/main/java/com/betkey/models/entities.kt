package com.betkey.models

import android.os.Parcel
import android.os.Parcelable
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

data class LotteryModel(
    var isSelected: Boolean,
    var number: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(number)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LotteryModel> {
        override fun createFromParcel(parcel: Parcel): LotteryModel {
            return LotteryModel(parcel)
        }

        override fun newArray(size: Int): Array<LotteryModel?> {
            return arrayOfNulls(size)
        }
    }
}