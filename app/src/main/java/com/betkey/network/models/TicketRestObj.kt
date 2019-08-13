package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class TicketRestObj (
    @SerializedName("ticket")
    var ticket: Ticket? = null,

    @SerializedName("statuses")
    var statuses: Map<String, String>? = null,

    @SerializedName("outcomes")
    var outcomes: Map<String, String>? = null
): MStatus()