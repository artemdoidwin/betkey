package com.betkey.ui

import com.betkey.base.BaseViewModel
import com.betkey.data.BetKeyDataManager
import com.betkey.data.MarginfoxDataManager
import com.betkey.data.PSPDataManager
import com.betkey.network.models.*
import com.betkey.utils.PHONE_CODE
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
    val agentDeposit = pspDataManager.agentDeposit
    val withdrawalConfirm = pspDataManager.withdrawalConfirm
    val withdrawalRequest = pspDataManager.withdrawalRequest
    val link = pspDataManager.link
    val restartScan = pspDataManager.restartScan
    val agentBet = marginfoxDataManager.agentBet
    val jackpotInfo = marginfoxDataManager.jackpotInfo
    val ticket = betkeydataManager.ticket
    val lookupBets = marginfoxDataManager.lookupBets
    val agent = betkeydataManager.agent
    val lotteryOrPick = betkeydataManager.lotteryOrPick
    val lotteryOrPickRequest = betkeydataManager.lotteryOrPickRequest
    val sportBetToday = marginfoxDataManager.sportBetToday
    val sportBetTomorrow = marginfoxDataManager.sportBetTomorrow
    val sportBetStartingSoon = marginfoxDataManager.sportBetStartingSoon
    val marketsRest = marginfoxDataManager.marketsRest
    val basketList = marginfoxDataManager.basketList
    val sportBettingStatus = marginfoxDataManager.sportBettingStatus
    val sportBetSuccess = marginfoxDataManager.sportBetSuccess

    var phoneNumberCountryCode = PHONE_CODE

    fun login(userName: String, password: String): Completable {
        return betkeydataManager.login(userName, password)
    }

    fun logout(): Completable {
        return betkeydataManager.agentLogout()
    }

    fun getJacpotInfo(): Single<JackpotInfo> {
        return marginfoxDataManager.getJackpotInfo()
    }

    fun sportBetToday(): Single<Map<String, Map<String, List<Event>>>> {
        return marginfoxDataManager.sportBetToday()
    }

    fun sportBetStartingSoon(): Single<Map<String, Map<String, List<Event>>>> {
        return marginfoxDataManager.sportBetStartingSoon()
    }

    fun sportBetTomorrow(): Single<Map<String, Map<String, List<Event>>>> {
        return marginfoxDataManager.sportBetTomorrow()
    }

    fun getSportbettingMarkets(eventId: String): Single<Event> {
        return marginfoxDataManager.getSportBettingMarkets(eventId)
    }

    fun getAgentProfile(stake: String): Single<TicketRestObj> {
        return marginfoxDataManager.getAgentProfile(stake).flatMap{
             checkTicket(it.code)
         }
    }

    fun publicBetslips(publicCode: String): Single<BetLookupObj> {
        return marginfoxDataManager.publicBetslips(publicCode)
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
        alternativeSelections: String
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

    fun agentWithdrawal(code: Int): Single<AgentWithdrawal> {
        val paymentId = (0..Int.MAX_VALUE).random()
        return pspDataManager.agentWithdrawalConfirm(paymentId, code)
    }

    fun agentWithdrawalRequest(securityCode: String): Single<WithdrawalRequest> {
        return pspDataManager.agentWithdrawalRequest(securityCode)
    }

    fun getOutcomes() = betkeydataManager.outcomes
}