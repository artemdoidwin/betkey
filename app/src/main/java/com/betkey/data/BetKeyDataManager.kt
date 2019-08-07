package com.betkey.data

import com.betkey.network.ApiInterfaceBetkey
import com.betkey.repository.ModelRepository
import io.reactivex.Completable

class BetKeyDataManager(
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiBetkey: ApiInterfaceBetkey
) {
    val wallets = modelRepository.wallets
    val player = modelRepository.player

    fun login(userName: String, password: String): Completable {
        return apiBetkey.authenticateAgent(userName, password)
            .flatMapCompletable {
                Completable.fromRunnable {
                    prefManager.saveToken(it.token)
                    modelRepository.agent.postWithValue(it.agent!!.agent)
                    modelRepository.wallets.postValue(it.wallets!!.toMutableList())
                }
            }
    }

    fun agentLogout(): Completable {
        return prefManager.getToken()!!.let { token ->
            apiBetkey.agentLogout(token)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        prefManager.saveToken("")
                    }
                }
        }
    }

    fun generateAgentToken() {
        modelRepository.agent.value?.also { agent ->
            apiBetkey.generateAgentToken(agent.id, agent.agentId, agent.username)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        prefManager.saveToken(it.token)
                    }
                }
        }
    }

    fun getAgentInfo() {
        prefManager.getToken()?.also {
            apiBetkey.getAgentInfo(it)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.agent.value = it.agent
                    }
                }
        }
    }

    fun getAgentWallets() {
        prefManager.getToken()?.also {
            apiBetkey.getAgentWallets(it)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.wallets.value = it.wallets
                    }
                }
        }
    }

    fun findPlayer(phone: String) : Completable{
        return apiBetkey.findPlayer(phone)
            .flatMapCompletable {
                val p = it.player
                Completable.fromRunnable {
                    modelRepository.player.postValue(it.player)
                }
            }

    }
}