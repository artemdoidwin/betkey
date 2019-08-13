package com.betkey.data

import com.betkey.network.ApiInterfaceBetkey
import com.betkey.network.models.PlayerRestObject
import com.betkey.network.models.TicketRestObj
import com.betkey.repository.ModelRepository
import io.reactivex.Completable
import io.reactivex.Single

class BetKeyDataManager(
    val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiBetkey: ApiInterfaceBetkey
) {
    val wallets = modelRepository.wallets
    val player = modelRepository.player
    val ticket = modelRepository.ticket

    fun login(userName: String, password: String): Completable {
        return apiBetkey.authenticateAgent(userName, password)
            .flatMapCompletable {
                Completable.fromRunnable {
                    prefManager.saveToken(it.token)
                    it.agent?.also {agent ->
                        prefManager.saveAgent(agent.id, agent.agentId, agent.username)
                    }
                    modelRepository.agent.postWithValue(it.agent!!)
                    modelRepository.wallets.postValue(it.wallets!!.toMutableList())
                }
            }
    }

    fun agentLogout(): Completable {
        return prefManager.getToken().let { token ->
            apiBetkey.agentLogout(token)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        prefManager.saveToken("")
                    }
                }
        }
    }

    fun getAgentInfo() {
        prefManager.getToken().also {
            apiBetkey.getAgentInfo(it)
                .flatMapCompletable {
                    Completable.fromRunnable {
                        modelRepository.agent.value = it.agent
                    }
                }
        }
    }

    fun getAgentWallets(): Completable {
        return apiBetkey.getAgentWallets( prefManager.getToken())
            .flatMapCompletable {
                Completable.fromRunnable {
                    modelRepository.wallets.postValue(it.wallets)
                }
            }
    }

    fun findPlayer(phone: String): Single<PlayerRestObject> {
        return apiBetkey.findPlayer(phone)
            .flatMap {
                modelRepository.player.postValue(it.player)
                Single.just(it)
            }
    }

    fun checkTicket(ticketCode: String): Single<TicketRestObj> {
        return prefManager.getToken().let { token ->
            apiBetkey.checkTicket(token, ticketCode)
                .flatMap {
                    it.ticket?.also { ticket ->
                        modelRepository.ticket.postValue(ticket)
                    }
                    Single.just(it)
                }
        }
    }
}