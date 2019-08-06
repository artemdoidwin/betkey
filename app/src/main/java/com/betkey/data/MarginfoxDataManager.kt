package com.betkey.data

import android.util.ArrayMap
import androidx.lifecycle.MutableLiveData
import com.betkey.network.ApiInterfaceMarginfox
import com.betkey.network.models.Event
import com.betkey.network.models.JackpotInfo
import com.betkey.repository.ModelRepository
import io.reactivex.Single
import java.util.*

class MarginfoxDataManager(
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiMarginfox: ApiInterfaceMarginfox
) {

    val betsDetailsList = MutableLiveData<List<Pair<String, String>>>().apply { value = null }

    fun getJacpotInfo(): Single<JackpotInfo> {
        return apiMarginfox.getJacpotInfo()
    }

}