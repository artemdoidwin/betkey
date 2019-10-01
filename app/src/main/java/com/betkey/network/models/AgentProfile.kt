package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class AgentProfile (
    @SerializedName("agent_document")
    var agentDocument: AgentDocument? = null,

    @SerializedName("betkey")
    var betkey: Betkey? = null
)