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
    val withdrawal = modelRepository.withdrawal
    val link = modelRepository.link

    fun agentDeposit(paymentId: Int, playerId: String, currency: String, amount: Int): Completable {
        return prefManager.getToken().let { token ->
            apiPSP.agentDeposit(token, paymentId, playerId, currency, amount)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.payment.postValue(it.payment)
                    }
                }
        }
    }

    fun agentWithdrawal(paymentId: Int, code: Int): Completable {
        return prefManager.getToken().let { token ->
            apiPSP.agentWithdrawal(token, paymentId, code)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.withdrawal.postValue(it)
                    }
                }
        }
    }
}