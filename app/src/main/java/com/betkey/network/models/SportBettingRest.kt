package com.betkey.network.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

data class SportBettingRest(
    @SerializedName("starting_soon")
    var startingSoon: Map<String, Map<String, List<LinkedTreeMap<String, String>>>> = mapOf(),

    @SerializedName("tomorrow")
    var tomorrow: Map<String, Map<String, List<LinkedTreeMap<String, String>>>> = mapOf(),

    @SerializedName("today")
    var today: Map<String, Map<String, List<LinkedTreeMap<String, String>>>> = mapOf()
)

//sportsMap<SportName, leaguesMap<LeagueName, List<Event>>>