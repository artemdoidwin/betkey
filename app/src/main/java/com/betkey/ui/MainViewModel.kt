package com.betkey.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.betkey.base.BaseViewModel
import com.betkey.data.BetKeyDataManager
import com.betkey.data.LocaleManager
import com.betkey.data.MarginfoxDataManager
import com.betkey.data.PSPDataManager
import com.betkey.models.PayoutModel
import com.betkey.network.models.*
import com.betkey.utils.PHONE_CODE
import io.reactivex.Completable
import io.reactivex.Single

class MainViewModel(
    private val betkeydataManager: BetKeyDataManager,
    private val marginfoxDataManager: MarginfoxDataManager,
    private val pspDataManager: PSPDataManager,
    private val localManager: LocaleManager
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
    val lookupBets2 = marginfoxDataManager.lookupBets2
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
    val printObj = marginfoxDataManager.printObj
    val report = betkeydataManager.report
    val payoutModel = marginfoxDataManager.payoutModel

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

    fun publicBetslips(publicCode: String): Single<BetLookupObj2> {
        return marginfoxDataManager.publicBetslips(publicCode)
    }

    fun getInstances(
    ): Single<Instance>{
        return marginfoxDataManager.getInstances()
    }

    fun jackpotAgentBetting(
        selections : ArrayList<String>,
        stake: Int,
        alternativeSelections: ArrayList<String>
    ): Single<AgentBettingResult> {
        return marginfoxDataManager.jackpotAgentBetting(
            selections, stake, alternativeSelections
        )
    }

    fun findPlayer(phone: String): Single<PlayerRestObject> {
        return betkeydataManager.findPlayer(phone)
    }

    fun checkTicket(ticketCode: String): Single<TicketRestObj> {
        return betkeydataManager.checkTicket(ticketCode)
    }

    fun betLookup(ticket: Ticket): Single<BetLookupObj> {
        return if(ticket.platformUnit.code != "jackpot") {
            marginfoxDataManager.betLookupBetslip(ticket.ticketId!!)
        } else {
            marginfoxDataManager.betLookupJackpot(ticket.ticketId!!)
        }
    }

    fun betLookup(ticketId: String): Single<BetLookupObj> {
        return marginfoxDataManager.betLookupJackpot(ticketId)
    }

    fun approveJackpot(code:String) = marginfoxDataManager.jackpotApprove(code)
    fun approveBetlsip(code:String) = marginfoxDataManager.betslipApprove(code).flatMap {  checkTicket(it.code) }
    fun getPrematchBetting() = betkeydataManager.getPrematchBetting()
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

    fun setNewLocale(c: Context, language: String) {
        localManager.setNewLocale(c, language)
    }

    fun getLocale() = localManager.getLocale()

    fun getReport(dateTimeFrom:String,dateTimeTo:String) = betkeydataManager.getReport(dateTimeFrom,dateTimeTo)

    fun payoutTicket(id: String) : Single<TicketPayout> {
        return marginfoxDataManager.payoutTicket(id)
    }
}