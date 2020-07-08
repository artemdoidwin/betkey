package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Bonus(

	@field:SerializedName("bonus")
	val bonus: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rule")
	val rule: Rule? = null
)