package com.betkey.ui

import com.betkey.base.BaseViewModel
import com.betkey.data.BetKeyDataManager
import com.betkey.data.MarginfoxDataManager
import com.betkey.network.models.JackpotInfo
import io.reactivex.Completable
import io.reactivex.Single

class MainViewModel(
    private val betkeydataManager: BetKeyDataManager,
    private val marginfoxDataManager: MarginfoxDataManager
    ) : BaseViewModel() {

    val betsDetailsList = marginfoxDataManager.betsDetailsList

    fun login(userName: String, password: String): Completable {
        return betkeydataManager.login(userName, password)
    }

    fun logout(): Completable {
        return betkeydataManager.agentLogout()
    }

    fun getJacpotInfo(): Single<JackpotInfo>{
        return marginfoxDataManager.getJacpotInfo()
    }


}