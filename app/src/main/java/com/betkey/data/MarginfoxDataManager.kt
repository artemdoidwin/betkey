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
    val sportBettingStatus = modelRepository.sportBetStatus
    val sportBetSuccess = modelRepository.sportBetSuccess

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

    fun getSportBettingMarkets(eventId: String): Single<Event> {
        return apiMarginfox.getSportbettingMarkets(eventId, "en", "exaloc_kong_key")
            .flatMap {
                modelRepository.marketsRest.postValue(it)
                Single.just(it)
            }
    }

    fun getAgentProfile(stake: String): Single<BetLookupObj?> {
        return prefManager.getToken().let { token ->
            apiMarginfox.getAgentProfile("exaloc_kong_key", "exaloc", token)
                .flatMap {
                    sportBettingPlaceBet(stake, it.message?.agentDocument?.id!!)
                }
        }
    }

    private fun sportBettingPlaceBet(stake: String, agentId: Int): Single<BetLookupObj?> {
        val events: HashMap<String, String> = hashMapOf()
        basketList.value?.also { basketList ->
            basketList.map { event ->
                events["betslip[events][${event.idEvent}][market]"] = event.marketKey
                events["betslip[events][${event.idEvent}][line]"] = event.lineName
                events["betslip[events][${event.idEvent}][bet]"] = event.betKey
                events["betslip[events][${event.idEvent}][odds]"] = event.odds
                events["betslip[events][${event.idEvent}][market_name]"] = event.marketName
            }
            events["betslip[stake]"] = stake
            events["betslip[source]"] = "HHT"
            events["betslip[instance]"] = "exaloc"
            events["betslip[live]"] = "0"
            events["betkeyData[agent_id]"] = agentId.toString()
        }
        return prefManager.getToken().let { token ->
            apiMarginfox.sprotBettingPlaceBet("exaloc_kong_key", events, token)
                .flatMap {
                    val model = BetLookupObj.checkStatus(it)
                    if (model == null) {
                        modelRepository.sportBetStatus.postValue(it.status)
                    } else {
                        modelRepository.sportBetSuccess.postValue(model)
                    }
                    Single.just(model)
                }
        }
    }

    fun publicBetslips(publicCode: String): Single<BetLookupObj> {
        return apiMarginfox.publicBetslips(publicCode)
            .flatMap {
                modelRepository.lookupBets.postValue(it)
                Single.just(it)
            }
    }
}