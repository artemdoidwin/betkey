package com.betkey.ui

import com.betkey.base.BaseViewModel
import com.betkey.data.BetKeyDataManager
import com.betkey.data.MarginfoxDataManager
import com.betkey.data.PSPDataManager
import com.betkey.network.models.JackpotInfo
import com.betkey.network.models.PlayerRestObject
import io.reactivex.Completable
import io.reactivex.Single

class MainViewModel(
    private val betkeydataManager: BetKeyDataManager,
    private val marginfoxDataManager: MarginfoxDataManager,
    private val pspDataManager: PSPDataManager
    ) : BaseViewModel() {

    val betsDetailsList = marginfoxDataManager.betsDetailsList
    val wallets = betkeydataManager.wallets
    val player = betkeydataManager.player
    val payment = pspDataManager.payment

    fun login(userName: String, password: String): Completable {
        return betkeydataManager.login(userName, password)
    }

    fun logout(): Completable {
        return betkeydataManager.agentLogout()
    }

    fun getJacpotInfo(): Single<JackpotInfo>{
        return marginfoxDataManager.getJackpotInfo()
    }

    fun findPlayer(phone: String): Single<PlayerRestObject> {
        return betkeydataManager.findPlayer(phone)
    }

    fun getAgentWallets(): Completable  {
        return betkeydataManager.getAgentWallets()
    }

    fun agentDeposit(playerId: String, currency: String, amount: Int): Completable {
        val paymentId = (0..Int.MAX_VALUE).random()
        return pspDataManager.agentDeposit(paymentId, playerId, currency, amount)
    }
}