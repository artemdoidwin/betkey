package com.betkey.network

import com.betkey.network.models.JackpotInfo
import io.reactivex.Single
import retrofit2.http.GET

interface ApiInterfaceMarginfox {

    @GET("coupon/events")
    fun getJacpotInfo(): Single<JackpotInfo>
}