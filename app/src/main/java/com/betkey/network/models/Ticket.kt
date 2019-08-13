package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class Ticket (
    @SerializedName("_id")
    var id: String? = null,

    @SerializedName("ticket_id")
    var ticketId: String? = null,

    @SerializedName("stake")
    var stake: String? = null,

    @SerializedName("currency")
    var currency: String? = null,

    @SerializedName("source")
    var source: String? = null,

    @SerializedName("agent")
    var agent: Agent? = null,

    @SerializedName("platform")
    var platform: Platform? = null,

    @SerializedName("platform_unit")
    var platformUnit: PlatformUnit? = null,

    @SerializedName("status")
    var status: Int? = null,

    @SerializedName("outcome")
    var outcome: Int? = null,

    @SerializedName("created")
    var created: Int? = null,

    @SerializedName("updated")
    var updated: Int? = null,

    @SerializedName("timeline")
    var timeline: List<TimeLine>? = null,

    @SerializedName("short_id")
    var shortId: String? = null

)