package com.betkey.data

import androidx.lifecycle.MutableLiveData
import com.betkey.network.ApiInterfaceMarginfox
import com.betkey.network.models.JackpotInfo
import com.betkey.repository.ModelRepository
import io.reactivex.Single

class MarginfoxDataManager(
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiMarginfox: ApiInterfaceMarginfox
) {

    val betsDetailsList = MutableLiveData<List<Pair<String, String>>>().apply { value = null }

    fun getJackpotInfo(): Single<JackpotInfo> {
        return apiMarginfox.getJacpotInfo()
    }

}