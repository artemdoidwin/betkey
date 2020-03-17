package com.betkey.network

import com.betkey.network.models.*
import io.reactivex.Single
import retrofit2.http.*

interface ApiInterfaceMarginfox {

    @GET("coupon/events")
    fun getJacpotInfo(): Single<JackpotInfo>

//    @Field("jackpot[selections][0]")
//    selections0: String,

    @FormUrlEncoded
    @POST("jackpot_bet/agent")
    fun jackpotAgentBetting(
        @Header("X-AUTH-TOKEN")
        token: String,
        @FieldMap
        selections: HashMap<String, String>,
        @Field("jackpot[stake]")
        stake: Int,
        @Field("jackpot[source]")
        source: String,
        @FieldMap
        alternativeSelections: HashMap<String, String>
    ): Single<AgentBettingResult>

    @GET("jackpot_bet/lookup/{betslip_code}")
    fun betLookupJackpot(
        @Path("betslip_code")
        betslip_code: String
    ): Single<BetLookupObj>

    @GET("public/betslips/{id}")
    fun betLookupBetslip(
        @Path("id")
        id: String
    ): Single<DummyBetLookupObj>


    @GET("prelive/static/pages")
    fun getSportbetting(
        @Query("lang") lang: String,
        @Query("markets") markets: String,
        @Query("api_key") apiKey: String
    ): Single<SportBettingRest>

    @GET("prelive/static/pages")
    fun getSportbetting(
        @Query("lang") lang: String,
        @Query("markets") markets: String,
        @Query("filter") filter: String,
        @Query("api_key") apiKey: String
    ): Single<SportBettingRest>

    @GET("iframes_settings/exaloc")
    fun getFeaturedEventIds(): Single<FeaturedEventIds>

    @GET("prelive/events/{ids}")
    fun getEventsById(
        @Path("ids") ids: String,
        @Query("lang") lang: String): Single<List<Event>>

    @GET("prelive/events/{event_id}")
    fun getSportbettingMarkets(
        @Path("event_id") eventId: String,
        @Query("lang") lang: String,
        @Query("api_key") apiKey: String
    ): Single<Event>

    @FormUrlEncoded
    @POST("betkey/agents/betslips")
    fun sprotBettingPlaceBet(
        @Query("api_key") apiKey: String,
        @FieldMap events: HashMap<String, String>,
        @Field("betkeyData[agent_token]") token: String
    ): Single<StatusBetslip>

    @FormUrlEncoded
    @POST("betkey/agent_profile")
    fun getAgentProfile(
        @Query("api_key") apiKey: String,
        @Field("betkey_agent_profile[instance]") instance: String,
        @Field("betkey_agent_profile[token]") token: String
    ): Single<AgentProfileRest>

    @GET("public/betslips/{public_code}")
    fun publicBetslips(
        @Path("public_code")
        publicCode: String
    ): Single<BetLookupObj2>


    @GET("instances/exaloc")
    fun getInstances(
    ): Single<Instance>
}