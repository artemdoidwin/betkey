package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("friendly_id")
    var friendlyId: Int? = null,

    @SerializedName("sport")
    var sport: String? = null,

    @SerializedName("start_time")
    var startTime: String? = null,

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("status_name")
    var statusName: String? = null,

    @SerializedName("blocked")
    var blocked: Boolean? = null,

    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @SerializedName("updated")
    var updated: Boolean? = null,

    @SerializedName("teams")
    var teams: Map<String, Team> = mapOf(),

    @SerializedName("league")
    var league: League? = null,

    @SerializedName("markets")
    var markets: Map<String, Market> = mapOf(),

    @SerializedName("result")
    var result: String? = null,

    @SerializedName("markets_count")
    var marketsCount: Int? = null,

    @SerializedName("bet")
    var bet: String? = null,

    @SerializedName("odds")
    var odds: Double? = null,

    @SerializedName("odds_formats")
    var odds_formats: Any? = null,

    @SerializedName("bet_time_info")
    var bet_time_info: Any? = null,

    @SerializedName("market")
    var market: String? = null,

    @SerializedName("market_name")
    var market_name: String? = null,

    @SerializedName("line")
    var line: String? = null,

    @SerializedName("time")
    var time: TimeObj? = null,

    var isAltGame: Boolean = false,
    var btnChecked: Int = -1
)