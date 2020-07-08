package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class TicketPayout(

	@field:SerializedName("error_message")
	val errorMessage: String? = null,

	@field:SerializedName("error_code")
	val errorCode: Int? = null,

	@field:SerializedName("message")
	val message: Message? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TotalOddsFormats(

	@field:SerializedName("dec")
	val dec: Double? = null,

	@field:SerializedName("us")
	val us: String? = null
)

data class Message(

	@field:SerializedName("gross_win")
	val grossWin: Double? = null,

	@field:SerializedName("win_status")
	val winStatus: Int? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("instance_name")
	val instanceName: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("payout")
	val payout: Double? = null,

	@field:SerializedName("tax")
	val tax: Double? = null,

	@field:SerializedName("bonus")
	val bonus: Bonus? = null,

	@field:SerializedName("shop_name")
	val shopName: String? = null,

	@field:SerializedName("total_odds")
	val totalOdds: Double? = null,

	@field:SerializedName("stake")
	val stake: Double? = null,

	@field:SerializedName("shop_id")
	val shopId: Int? = null,

	@field:SerializedName("cancel_reason")
	val cancelReason: String? = null,

	@field:SerializedName("approved")
	val approved: Boolean? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("campaign")
	val campaign: List<Any?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("total_odds_formats")
	val totalOddsFormats: TotalOddsFormats? = null,

	@field:SerializedName("updated")
	val updated: String? = null,

	@field:SerializedName("events")
	val events: List<EventsItem?>? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("live_status")
	val liveStatus: Boolean? = null
)

data class Teams(

	@field:SerializedName("1")
	val jsonMember1: JsonMember1? = null,

	@field:SerializedName("2")
	val jsonMember2: JsonMember2? = null
)

data class EventsItem(

	@field:SerializedName("teams")
	val teams: Teams? = null,

	@field:SerializedName("line")
	val line: String? = null,

	@field:SerializedName("league")
	val league: League? = null,

	@field:SerializedName("market_name")
	val marketName: String? = null,

	@field:SerializedName("market")
	val market: String? = null,

	@field:SerializedName("bet")
	val bet: String? = null,

	@field:SerializedName("friendly_id")
	val friendlyId: Int? = null,

	@field:SerializedName("odds")
	val odds: Double? = null,

	@field:SerializedName("odds_formats")
	val oddsFormats: OddsFormats? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("time")
	val time: Time? = null,

	@field:SerializedName("sport")
	val sport: String? = null,

	@field:SerializedName("updated")
	val updated: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class JsonMember2(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)

data class JsonMember1(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)

data class Time(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("timezone_type")
	val timezoneType: Int? = null
)
