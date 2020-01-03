package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
	@SerializedName("translations")
	val translations: Map<String?, Map<String?, String?>?>? = null, // Map<Language, Map<Key, Translation>>
	@SerializedName("message")
	val message: String? = null,
	@SerializedName("errors")
	val errors: List<Any?>? = null,
	@SerializedName("status")
	val status: Boolean? = null
)