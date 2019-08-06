package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class Event (
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

    @SerializedName("teams")
    var teams: Map<String, Team>,

    @SerializedName("league")
    var league: League? = null,

    @SerializedName("markets")
    var markets: Map<String, Market>,

    @SerializedName("result")
    var result: String? = null,

    @SerializedName("markets_count")
    var marketsCount: Int? = null
)