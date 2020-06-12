package com.betkey.network.models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.random.Random

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
    var bet: String = "",

    @SerializedName("odds")
    var odds: Double? = null,

    @SerializedName("odds_formats")
    var odds_formats: OddsFormats? = null,

    @SerializedName("bet_time_info")
    var bet_time_info: Any? = null,

    @SerializedName("market")
    var market: String = "",

    @SerializedName("market_name")
    var market_name: String = "",

    @SerializedName("line")
    var line: String = "",

    @SerializedName("time")
    var time: TimeObj? = null,

    var isAltGame: Boolean = false,
    var btnChecked: Int = -1
){
    fun generateEvent() = Event(
        id = Random(5).nextInt().toString(),
        friendlyId = Random(5).nextInt(),
        sport = "football",
        startTime = "2019-04-16T17:00:00+00:00",
        status = 0,
        statusName = "Scheduled",
        updatedAt = "2019-04-16T00:50:57+00:00",
        teams = mapOf(Pair("1", Team(UUID.randomUUID().toString(),"firstTeam")),Pair("2", Team(UUID.randomUUID().toString(),"secondTeam"))),
        league = League(id = UUID.randomUUID().toString(),
            name = "Superettan",
            order = 9999),
        blocked = false,
        odds = 0.44,
        result = "Fool Time",
        odds_formats = OddsFormats("2.33","of")

        )
}

