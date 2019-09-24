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
                startingSoon = sportBettingRest.startingSoon.map { map1 ->
                    map1.key to map1.value.map { map2 ->
                        map2.key to map2.value.map { linked ->
                            Gson().fromJson(Gson().toJson(linked), Event::class.java)
                        }
                    }.toMap()
                }.toMap(),
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

    }
}

//                            Event(
//                                id = event.id,
//                                friendlyId = event.friendlyId,
//                                sport = event.sport,
//                                startTime = event.startTime,
//                                status = event.status,
//                                statusName = event.statusName,
//                                blocked = event.blocked,
//                                updatedAt = event.updatedAt,
//                                updated = event.updated,
//                                teams = event.teams,
//                                league = event.league,
//                                markets = event.markets,
//                                result = event.result,
//                                marketsCount = event.marketsCount,
//                                bet = event.bet,
//                                odds = event.odds,
//                                odds_formats = event.odds_formats,
//                                bet_time_info = event.bet_time_info,
//                                market = event.market,
//                                market_name = event.market_name,
//                                line = event.line,
//                                time = event.time,
//                                isAltGame = false,
//                                btnChecked = -1
//                            )











