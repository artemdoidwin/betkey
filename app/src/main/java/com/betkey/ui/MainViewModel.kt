package com.betkey.ui

import com.betkey.base.BaseViewModel
import com.betkey.data.BetKeyDataManager
import com.betkey.data.MarginfoxDataManager
import com.betkey.data.PSPDataManager
import com.betkey.network.models.*
import com.betkey.utils.AGENT_HHT
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
    val link = pspDataManager.link
    val agentBet = marginfoxDataManager.agentBet
    val jackpotInfo = marginfoxDataManager.jackpotInfo
    val ticket = betkeydataManager.ticket
    val lookupBets = marginfoxDataManager.lookupBets
    val agent = betkeydataManager.agent

    var phoneNumberCountryCode = 237

    fun login(userName: String, password: String): Completable {
        return betkeydataManager.login(userName, password)
    }

    fun logout(): Completable {
        return betkeydataManager.agentLogout()
    }

    fun getJacpotInfo(): Single<JackpotInfo> {
        return marginfoxDataManager.getJackpotInfo()
    }

    fun jackpotAgentBetting(
        selection0: String,
        selection1: String,
        selection2: String,
        selection3: String,
        selection4: String,
        selection5: String,
        selection6: String,
        stake: Int,
        alternativeSelections: Int
    ): Single<AgentBettingResult> {
        return marginfoxDataManager.jackpotAgentBetting(
            selection0, selection1, selection2, selection3, selection4,
            selection5, selection6, stake, alternativeSelections
        )
    }

    fun findPlayer(phone: String): Single<PlayerRestObject> {
        return betkeydataManager.findPlayer(phone)
    }

    fun checkTicket(ticketCode: String): Single<TicketRestObj> {
        return betkeydataManager.checkTicket(ticketCode)
    }

    fun betLookup(ticketId: String): Single<BetLookupObj> {
        return marginfoxDataManager.betLookup(ticketId)
    }

    fun getAgentWallets(): Completable {
        return betkeydataManager.getAgentWallets()
    }

    fun agentDeposit(playerId: String, currency: String, amount: Int): Completable {
        val paymentId = (0..Int.MAX_VALUE).random()
        return pspDataManager.agentDeposit(paymentId, playerId, currency, amount)
    }

    fun getOutcomes() = betkeydataManager.outcomes
}