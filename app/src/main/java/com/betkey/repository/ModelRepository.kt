package com.betkey.repository

import androidx.lifecycle.MutableLiveData
import com.betkey.models.LotteryOrPickModel
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.*
import com.betkey.utils.SingleLiveEvent

class ModelRepository {
    val agent = SingleLiveEvent<Agent>().apply { value = null }
    val wallets = MutableLiveData<MutableList<Wallet>>().apply { value = null }
    val events = MutableLiveData<Map<String, Event>>().apply { value = null }
    val player = MutableLiveData<Player>().apply { value = null }
    val agentDeposit = MutableLiveData<PaymentRest>().apply { value = null }
    val withdrawalConfirm = MutableLiveData<AgentWithdrawal>().apply { value = null }
    val withdrawalRequest = MutableLiveData<WithdrawalRequest>().apply { value = null }
    val link = SingleLiveEvent<String>().apply { value = null }
    val agentBet = MutableLiveData<AgentBettingResult>().apply { value = null }
    val sportBetStartingSoon =
        MutableLiveData<Map<String, Map<String, List<Event>>>>().apply { value = null }
    val sportBetTomorrow =
        MutableLiveData<Map<String, Map<String, List<Event>>>>().apply { value = null }
    val sportBetToday =
        MutableLiveData<Map<String, Map<String, List<Event>>>>().apply { value = null }
    val jackpotInfo = MutableLiveData<JackpotInfo>().apply { value = null }
    val ticket = MutableLiveData<Ticket>().apply { value = null }
    val lookupBets = MutableLiveData<BetLookupObj>().apply { value = null }
    val lotteryOrPick = MutableLiveData<LotteryOrPickModel>().apply { value = null }
    val lotteryOrPickRequest = MutableLiveData<String>().apply { value = null }
    val marketsRest = MutableLiveData<Event>().apply { value = null }
    val sportBetStatus = MutableLiveData<Int>().apply { value = null }
    val sportBetSuccess = MutableLiveData<BetLookupObj>().apply { value = null }
    val basketList = MutableLiveData<MutableList<SportBetBasketModel>>().apply { value = mutableListOf()}
}


