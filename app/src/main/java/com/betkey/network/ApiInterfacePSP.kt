package com.betkey.network

import com.betkey.network.models.AgentDeposit
import com.betkey.network.models.AgentWithdrawal
import com.betkey.network.models.WithdrawalRequest
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

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

    @FormUrlEncoded
    @POST("agent/withdrawal_confirm")
    fun agentWithdrawalConfirm(
        @Header("X-AUTH-TOKEN")
        token: String,

        @Field("payment[id]")
        paymentId: Int,

        @Field("payment[code]")
        code: Int
    ): Single<AgentWithdrawal>

    @POST("agent/withdrawal_request_info/{security_code}")
    fun agentWithdrawalRequest(
        @Header("X-AUTH-TOKEN")
        token: String,

        @Path("security_code")
        securityCode: String
    ): Single<WithdrawalRequest>
}