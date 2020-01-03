package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class FeaturedEventIds(

	@field:SerializedName("result")
	val result: Result? = null
)

data class Result(

	@field:SerializedName("general")
	val general: Any? = null,

	@field:SerializedName("ad_widget_page")
	val adWidgetPage: AdWidgetPage? = null,

	@field:SerializedName("front_page")
	val frontPage: FrontPage? = null,

	@field:SerializedName("jackpot_banners")
	val jackpotBanners: Any? = null
)

data class AdWidgetPage(

	@field:SerializedName("top")
	val top: List<TopItem?>? = null,

	@field:SerializedName("left")
	val left: List<LeftItem?>? = null,

	@field:SerializedName("right")
	val right: List<RightItem?>? = null
)

data class FrontPage(

	@field:SerializedName("featured_matches_ids")
	val featuredMatchesIds: List<String?>? = null,

	@field:SerializedName("competition_ids")
	val competitionIds: List<String?>? = null
)

data class LeftItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("rackspace_url")
	val rackspaceUrl: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class RightItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("rackspace_url")
	val rackspaceUrl: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class TopItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("rackspace_url")
	val rackspaceUrl: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)