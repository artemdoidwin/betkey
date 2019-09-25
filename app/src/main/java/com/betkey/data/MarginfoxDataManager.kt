package com.betkey.data

import androidx.lifecycle.MutableLiveData
import com.betkey.network.ApiInterfaceMarginfox
import com.betkey.network.models.*
import com.betkey.network.models.SportBetting.Companion.toSportBetting
import com.betkey.repository.ModelRepository
import com.betkey.utils.AGENT_HHT
import io.reactivex.Single

class MarginfoxDataManager(
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiMarginfox: ApiInterfaceMarginfox
) {

    val betsDetailsList = MutableLiveData<List<Pair<String, String>>>().apply { value = null }
    val agentBet = modelRepository.agentBet
    val jackpotInfo = modelRepository.jackpotInfo
    val lookupBets = modelRepository.lookupBets
    val sportBetStartingSoon = modelRepository.sportBetStartingSoon
    val sportBetTomorrow = modelRepository.sportBetTomorrow
    val sportBetToday = modelRepository.sportBetToday
    val marketsRest = modelRepository.marketsRest
    val basketList = modelRepository.basketList

    fun getJackpotInfo(): Single<JackpotInfo> {
        return apiMarginfox.getJacpotInfo()
            .flatMap {
                modelRepository.jackpotInfo.postValue(it)
                Single.just(it)
            }
    }

    fun betLookup(ticketId: String): Single<BetLookupObj> {
        return apiMarginfox.betLookup(ticketId)
            .flatMap {
                modelRepository.lookupBets.postValue(it)
                Single.just(it)
            }
    }

    fun jackpotAgentBetting(
        selection0: String,
        selection1: String,
        selection2: String,
        selection3: String,
        selection4: String,
        selection5: String,
        selection6: String,
        stake: Int,
        alternativeSelections: String
    ): Single<AgentBettingResult> {
        return prefManager.getToken().let { token ->
            apiMarginfox.jackpotAgentBetting(
                token, selection0, selection1, selection2, selection3, selection4,
                selection5, selection6, stake, AGENT_HHT, alternativeSelections
            )
                .flatMap {
                    modelRepository.agentBet.postValue(it)
                    Single.just(it)
                }
        }
    }

    fun sportBetToday(): Single<Map<String, Map<String, List<Event>>>> {
        return apiMarginfox.getSportbetting("en", "MRFT", "exaloc_kong_key")
            .flatMap {
                val sb = toSportBetting(it)
                modelRepository.sportBetToday.postValue(sb.today)
                Single.just(sb.today)
            }
    }

    fun sportBetTomorrow(): Single<Map<String, Map<String, List<Event>>>> {
        return apiMarginfox.getSportbetting("en", "MRFT", "exaloc_kong_key")
            .flatMap {
                val sb = toSportBetting(it)
                modelRepository.sportBetTomorrow.postValue(sb.tomorrow)
                Single.just(sb.tomorrow)
            }
    }

    fun sportBetStartingSoon(): Single<Map<String, Map<String, List<Event>>>> {
        return apiMarginfox.getSportbetting("en", "MRFT", "exaloc_kong_key")
            .flatMap {
                val sb = toSportBetting(it)
                modelRepository.sportBetStartingSoon.postValue(sb.startingSoon)
                Single.just(sb.startingSoon)
            }
    }

    fun getSportbettingMarkets(eventId: String): Single<Event> {
        return apiMarginfox.getSportbettingMarkets(eventId, "en", "exaloc_kong_key")
            .flatMap {
                modelRepository.marketsRest.postValue(it)
                Single.just(it)
            }
    }
}