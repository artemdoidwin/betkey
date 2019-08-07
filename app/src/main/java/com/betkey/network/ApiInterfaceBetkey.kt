package com.betkey.network

import com.betkey.network.models.*
import io.reactivex.Single
import retrofit2.http.*

interface ApiInterfaceBetkey {

    @FormUrlEncoded
    @POST("agents/authenticate")
    fun authenticateAgent(@Field("agent[username]") userName: String, @Field("agent[password]") password: String): Single<AuthenticateAgent>

    @FormUrlEncoded
    @POST("agents/authenticate/token")
    fun generateAgentToken(
        @Field("agent[id]")
        agentId: String,

        @Field("agent[agent_id]")
        agent_id: Int,

        @Field("agent[username]")
        userName: String
    ): Single<TokenObject>

    @POST("agents/logout")
    fun agentLogout(@Header("X-AUTH-TOKEN") token: String): Single<MStatus>

    @FormUrlEncoded
    @POST("agents/info")
    fun getAgentInfo(@Header("X-AUTH-TOKEN") token: String): Single<AgentRestObject>

    @GET("agents/wallets")
    fun getAgentWallets(@Header("X-AUTH-TOKEN") token: String): Single<Wallets>

    @FormUrlEncoded
    @POST("players/lookup")
    fun findPlayer( @Field("player[lookup]") phone: String): Single<PlayerRestObject>
}