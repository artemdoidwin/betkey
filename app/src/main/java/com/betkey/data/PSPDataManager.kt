package com.betkey.data

import com.betkey.network.ApiInterfacePSP
import com.betkey.repository.ModelRepository
import io.reactivex.Completable

class PSPDataManager (
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiPSP: ApiInterfacePSP
) {

    val payment = modelRepository.payment

    fun agentDeposit(paymentId: Int, playerId: String, currency: String, amount: Int): Completable {
        return prefManager.getToken()!!.let { token ->
            apiPSP.agentDeposit(token, paymentId, playerId, currency, amount)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.payment.postValue(it.payment)
                    }
                }
        }
    }


}