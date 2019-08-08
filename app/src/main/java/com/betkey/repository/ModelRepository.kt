package com.betkey.repository

import androidx.lifecycle.MutableLiveData
import com.betkey.data.PreferencesManager
import com.betkey.network.models.*
import com.betkey.utils.SingleLiveEvent

class ModelRepository(
    private val preffsManager: PreferencesManager
) {

    val agent = SingleLiveEvent<Agent>().apply { value = null }
    val wallets = MutableLiveData<MutableList<Wallet>>().apply { value = null }
    val events = MutableLiveData<Map<String, Event>>().apply { value = null }
    val player = MutableLiveData<Player>().apply { value = null }
    val payment = MutableLiveData<PaymentRest>().apply { value = null }
    val link = SingleLiveEvent<String>().apply { value = null }
    val agentBet = MutableLiveData<AgentBettingResult>().apply { value = null }
    val jackpotInfo = MutableLiveData<JackpotInfo>().apply { value = null }


}


