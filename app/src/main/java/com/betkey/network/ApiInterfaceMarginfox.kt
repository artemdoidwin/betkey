package com.betkey.network

import com.betkey.network.models.*
import io.reactivex.Single
import retrofit2.http.*

interface ApiInterfaceMarginfox {

    @GET("coupon/events")
    fun getJacpotInfo(): Single<JackpotInfo>

    @FormUrlEncoded
    @POST("jackpot_bet/agent")
    fun jackpotAgentBetting(
        @Header("X-AUTH-TOKEN")
        token: String,

        @Field("jackpot[selections][0]")
        selections0: String,

        @Field("jackpot[selections][1]")
        selections1: String,

        @Field("jackpot[selections][2]")
        selections2: String,

        @Field("jackpot[selections][3]")
        selections3: String,

        @Field("jackpot[selections][4]")
        selections4: String,

        @Field("jackpot[selections][5]")
        selections5: String,

        @Field("jackpot[selections][6]")
        selections6: String,

        @Field("jackpot[stake]")
        stake: Int,

        @Field("jackpot[source]")
        source: String,

        @Field("jackpot[alternativeSelections][0]")
        alternativeSelections: String
    ): Single<AgentBettingResult>

    @GET("jackpot_bet/lookup/{betslip_code}")
    fun betLookup(
        @Path("betslip_code")
        betslip_code: String
    ): Single<BetLookupObj>

    @GET("prelive/static/pages")
    fun getSportbetting(
        @Query("lang") lang: String,
        @Query("markets") markets: String,
        @Query("api_key") apiKey: String
    ): Single<SportBettingRest>

    @GET("prelive/events/{event_id}")
    fun getSportbettingMarkets(
        @Path("event_id") eventId: String,
        @Query("lang") lang: String,
        @Query("api_key") apiKey: String
    ): Single<Event>
}