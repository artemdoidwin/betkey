package com.betkey.repository

import androidx.lifecycle.MutableLiveData
import com.betkey.data.PreferencesManager
import com.betkey.network.models.Agent
import com.betkey.network.models.Event
import com.betkey.network.models.Wallet
import com.betkey.utils.SingleLiveEvent

class ModelRepository(
    private val preffsManager: PreferencesManager
) {

    val agent = SingleLiveEvent<Agent>().apply { value = null }
    val wallets = SingleLiveEvent<MutableList<Wallet>>().apply { value = null }
    val events = MutableLiveData<Map<String, Event>>().apply { value = null }



}


