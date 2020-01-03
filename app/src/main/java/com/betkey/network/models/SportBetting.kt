package com.betkey.network.models

import com.google.gson.Gson

class SportBetting(
    var startingSoon: Map<String, Map<String, List<Event>>> = mapOf(),
    var tomorrow: Map<String, Map<String, List<Event>>> = mapOf(),
    var today: Map<String, Map<String, List<Event>>> = mapOf()
) {
    companion object {
        fun toSportBetting(sportBettingRest: SportBettingRest) =
            SportBetting(
                tomorrow = sportBettingRest.tomorrow.map { map1 ->
                    map1.key to map1.value.map { map2 ->
                        map2.key to map2.value.map { linked ->
                            Gson().fromJson(Gson().toJson(linked), Event::class.java)
                        }
                    }.toMap()
                }.toMap(),
                today = sportBettingRest.today.map { map1 ->
                    map1.key to map1.value.map { map2 ->
                        map2.key to map2.value.map { linked ->
                            Gson().fromJson(Gson().toJson(linked), Event::class.java)
                        }
                    }.toMap()
                }.toMap()

            )

        fun toFeaturedEvents(events: List<Event>): SportBetting{

            val eventsMap: MutableMap<String, MutableList<Event>> = mutableMapOf()
            events.forEach { event ->
                if(!eventsMap.containsKey(event.league?.name) && !event.league?.name.isNullOrEmpty()) {
                    event.league?.name?.also { league ->
                        eventsMap[league] = mutableListOf()
                    }
                }
                eventsMap[event.league?.name]?.add(event)
            }

            return SportBetting(startingSoon = mapOf<String, Map<String, List<Event>>>("football" to eventsMap))
        }

    }
}











