package com.betkey.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.betkey.network.ApiInterfaceMarginfox
import com.betkey.network.models.*
import com.betkey.network.models.SportBetting.Companion.toFeaturedEvents
import com.betkey.network.models.SportBetting.Companion.toSportBetting
import com.betkey.repository.ModelRepository
import com.betkey.utils.AGENT_HHT
import com.betkey.utils.API_KEY_MARGINFOX
import com.betkey.utils.INSTANCE_MARGINFOX
import io.reactivex.Single
import org.jetbrains.anko.collections.forEachWithIndex

class MarginfoxDataManager(
    private val prefManager: PreferencesManager,
    private val modelRepository: ModelRepository,
    private val apiMarginfox: ApiInterfaceMarginfox
) {

    val betsDetailsList = MutableLiveData<List<Pair<String, String>>>().apply { value = null }
    val agentBet = modelRepository.agentBet
    val jackpotInfo = modelRepository.jackpotInfo
    val lookupBets = modelRepository.lookupBets
    val lookupBets2 = modelRepository.lookupBets2
    val sportBetStartingSoon = modelRepository.sportBetStartingSoon.apply {
       // this.value = mapOf(Pair("ijoijdf", mapOf(Pair("jjjfkj", listOf(Event().generateEvent())))))
    }
    val sportBetTomorrow = modelRepository.sportBetTomorrow
    val sportBetToday = modelRepository.sportBetToday
    val marketsRest = modelRepository.marketsRest
    val basketList = modelRepository.basketList
    val sportBettingStatus = modelRepository.sportBetStatus
    val sportBetSuccess = modelRepository.sportBetSuccess
    val printObj = modelRepository.printObj
    val payoutModel = modelRepository.payoutModel

    fun getJackpotInfo(): Single<JackpotInfo> {
        return apiMarginfox.getJacpotInfo()
            .flatMap {
                modelRepository.jackpotInfo.postValue(it)
                Single.just(it)
            }
    }

    fun betLookupJackpot(ticketId: String): Single<BetLookupObj> {
        return apiMarginfox.betLookupJackpot(ticketId)
            .flatMap {
                modelRepository.lookupBets.postValue(it)
                Single.just(it)
            }
    }

    fun betLookupBetslip(ticketId: String): Single<BetLookupObj> {
        return apiMarginfox.betLookupBetslip(ticketId)
            .flatMap {dummy ->
                val obj = BetLookupObj.toBetLookup(dummy)
                modelRepository.lookupBets.postValue(obj)
                Single.just(obj)
            }
    }

    fun jackpotApprove(code:String): Single<ApproveJackpotResponse> {
        prefManager.getToken().let {
            return apiMarginfox.approveJackpot(it,code)
        }

    }
    fun betslipApprove(code:String): Single<BetLookupObj> {
        prefManager.getToken().let {
            return apiMarginfox.approveBetslip(it,code).flatMap {
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



    fun jackpotAgentBetting(
        selections: ArrayList<String>,
        stake: Int,
        alternativeSelections: ArrayList<String>
    ): Single<AgentBettingResult> {
        Log.d("eventss","send jackpotInfo.events ${selections}  jackpotInfo.altEvents ${alternativeSelections}")
        val map = linkedMapOf<String,String>()
        selections.forEachWithIndex { index, value ->
            map["jackpot[selections][${index}]"] = value
        }

        val alternativesMap = linkedMapOf<String,String>()
        alternativeSelections.forEachWithIndex { index, value ->
            alternativesMap["jackpot[alternativeSelections][${index}]"] = value
        }

        return prefManager.getToken().let { token ->
            apiMarginfox.jackpotAgentBetting(
                token, map, stake, AGENT_HHT, alternativesMap
            )
                .flatMap {
                    modelRepository.agentBet.postValue(it.apply { it.created = it.created*1000 })
                    Single.just(it)
                }
        }
    }

    fun sportBetToday(): Single<Map<String, Map<String, List<Event>>>> {
        return apiMarginfox.getSportbetting(prefManager.getLanguage(), "MRFT","today", API_KEY_MARGINFOX)
            .flatMap {
                val sb = toSportBetting(it)
                modelRepository.sportBetToday.postValue(sb.today)
                Single.just(sb.today)
            }
    }

    fun sportBetTomorrow(): Single<Map<String, Map<String, List<Event>>>> {
        return apiMarginfox.getSportbetting(prefManager.getLanguage(), "MRFT","tomorrow", API_KEY_MARGINFOX)
            .flatMap {
                val sb = toSportBetting(it)
                modelRepository.sportBetTomorrow.postValue(sb.tomorrow)
                Single.just(sb.tomorrow)
            }
    }

    fun sportBetStartingSoon(): Single<Map<String, Map<String, List<Event>>>> {
        return apiMarginfox.getSportbetting(prefManager.getLanguage(), "MRFT","starting_soon", API_KEY_MARGINFOX)
            .flatMap {

                val sb = toSportBetting(it)
                Log.d("soon", "soo $sb")
                modelRepository.sportBetStartingSoon.postValue(sb.startingSoon)
                Single.just(sb.startingSoon)
            }
    }

    fun getSportBettingMarkets(eventId: String): Single<Event> {
        return apiMarginfox.getSportbettingMarkets(eventId, prefManager.getLanguage(), API_KEY_MARGINFOX)
            .flatMap {
                modelRepository.marketsRest.postValue(it)
                Single.just(it)
            }
    }

    fun getAgentProfile(stake: String): Single<BetLookupObj?> {
//        return prefManager.getToken().let { token ->
//            apiMarginfox.getAgentProfile(API_KEY_MARGINFOX, "betoo", token)
//                .flatMap {
//                    sportBettingPlaceBet(stake, it.message?.agentDocument?.id!!)
//                }
//            apiMarginfox.
//        }
        val id =  prefManager.getId()
        return sportBettingPlaceBet(stake, id)
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

        }

        return prefManager.getToken().let { token ->
            Log.d("CreatingTicket"," betkeyData[agent_token] = $token")
            apiMarginfox.getAgentProfile(API_KEY_MARGINFOX,"exaloc",token).flatMap {
                events["betkeyData[agent_id]"] = it.message?.agentDocument?.id.toString()
                events.forEach {
                    Log.d("CreatingTicket"," ${it.key} = ${it.value}")
                }

                apiMarginfox.sprotBettingPlaceBet(API_KEY_MARGINFOX, events, token)
                    .flatMap {
                        Log.d("sprotBettingPlaceBet","StatusBetslip: $it")
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
    }

    fun publicBetslips(publicCode: String): Single<BetLookupObj2> {
        return apiMarginfox.publicBetslips(publicCode)
            .flatMap {
                modelRepository.lookupBets2.postValue(it)
                Single.just(it)
            }
    }

    fun getInstances(
    ): Single<Instance>{
       return apiMarginfox.getInstances()
    }

    fun payoutTicket(id: String) : Single<TicketPayout> {
       return prefManager.getToken().let { token ->
           apiMarginfox.payoutTicket(id, API_KEY_MARGINFOX, token)
       }
    }
}