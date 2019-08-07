package com.betkey.network

import com.betkey.network.models.AgentDeposit
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterfacePSP {

    @FormUrlEncoded
    @POST("agent/deposit")
    fun agentDeposit(
        @Header("X-AUTH-TOKEN")
        token: String,

        @Field("payment[id]")
        paymentId: Int,

        @Field("payment[player_id]")
        playerId: String,

        @Field("payment[currency]")
        currency: String,

        @Field("payment[amount]")
        amount: Int
    ): Single<AgentDeposit>
}