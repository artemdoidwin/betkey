package com.betkey.data

import com.betkey.network.ApiInterfacePSP
import com.betkey.network.models.AgentWithdrawal
import com.betkey.network.models.WithdrawalRequest
import com.betkey.repository.ModelRepository
import io.reactivex.Completable
import io.reactivex.Single

class PSPDataManager(
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiPSP: ApiInterfacePSP
) {

    val agentDeposit = modelRepository.agentDeposit
    val withdrawalConfirm = modelRepository.withdrawalConfirm
    val withdrawalRequest = modelRepository.withdrawalRequest
    val link = modelRepository.link

    fun agentDeposit(paymentId: Int, playerId: String, currency: String, amount: Int): Completable {
        return prefManager.getToken().let { token ->
            apiPSP.agentDeposit(token, paymentId, playerId, currency, amount)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.agentDeposit.postValue(it.payment)
                    }
                }
        }
    }

    fun agentWithdrawalConfirm(paymentId: Int, code: Int): Single<AgentWithdrawal> {
        return prefManager.getToken().let { token ->
            apiPSP.agentWithdrawalConfirm(token, paymentId, code)
                .flatMap {
                    modelRepository.withdrawalConfirm.postValue(it)
                    Single.just(it)
                }
        }
    }

    fun agentWithdrawalRequest(securityCode: String): Single<WithdrawalRequest> {
        return prefManager.getToken().let { token ->
            apiPSP.agentWithdrawalRequest(token, securityCode)
                .flatMap {
                    modelRepository.withdrawalRequest.postValue(it)
                    Single.just(it)
                }
        }
    }
}