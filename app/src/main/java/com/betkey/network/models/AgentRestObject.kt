package com.betkey.network.models

import com.google.gson.annotations.SerializedName

data class AgentRestObject (
    @SerializedName("agent")
    val agent: Agent
)