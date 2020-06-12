package com.betkey.ui.jackpot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.Bet
import com.betkey.network.models.BetLookupObj
import com.betkey.network.models.Event
import com.betkey.network.models.JackpotInfo
import com.betkey.ui.MainViewModel
import com.betkey.utils.*
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jackpot.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import retrofit2.http.Tag
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class JackpotFragment(private val betLookup: BetLookupObj? = null) : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "JackpotFragment"

        fun newInstance(betLookup: BetLookupObj? = null) = JackpotFragment(betLookup = betLookup)
    }

    private lateinit var gamesAdapter: JackpotGamesAdapter
    private lateinit var productsListener: GameListener
    private var betDetailsMap = HashMap<String, String>()
    private var altDetailsMap = HashMap<String, String>()

    private var stake: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jackpot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsListener = object : GameListener {
            override fun onCommandLeft(commandName: String, bet: Bet, selection: String, isAlternativeEvent: Boolean) {
                if(isAlternativeEvent) {
                    altDetailsMap[commandName] = bet.name
                } else {
                    betDetailsMap[commandName] = bet.name
                }
                if (betDetailsMap.size + altDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }

            override fun onIDraw(commandName: String, bet: Bet, selection: String, isAlternativeEvent: Boolean) {
                if(isAlternativeEvent) {
                    altDetailsMap[commandName] = bet.name
                } else {
                    betDetailsMap[commandName] = bet.name
                }
                if (betDetailsMap.size + altDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }

            override fun onCommandRight(commandName: String, bet: Bet, selection: String, isAlternativeEvent: Boolean) {
                if(isAlternativeEvent) {
                    altDetailsMap[commandName] = bet.name
                } else {
                    betDetailsMap[commandName] = bet.name
                }
                if (betDetailsMap.size + altDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }
        }
        gamesAdapter = JackpotGamesAdapter(productsListener)
        jackpot_games_adapter.adapter = gamesAdapter

        compositeDisposable.add(
            jackpot_create_ticket_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (!isLowBattery(context!!)){
                    val comparator = Comparator<Pair<String, String>> { o1, o2 ->
                            when {
                                o1.first > o2.first -> 1
                                o1.first == o2.first -> 0
                                else -> -1
                            }
                        }

                    val eventsList = betDetailsMap.toList().toMutableList()
                    eventsList.sortWith(comparator)

                    val altEventsList = altDetailsMap.toList().toMutableList()
                    eventsList.sortWith(comparator)

                    stake = jackpot_stake_sp.selectedItem.toString().toInt()
                    sendRequest(eventsList as ArrayList<Pair<String, String>>,
                        altEventsList as ArrayList<Pair<String, String>>
                    )
                }else{context?.also {con -> toast("low battary")}}
            }
        )

        if (betLookup != null){
            subscribe(viewModel.getJacpotInfo(), {
                setJackpotInfo(betLookup,it)
            }, {context?.also {con -> toast(setMessage(it, con))}})
        }else{
            subscribe(viewModel.getJacpotInfo(), {
                Log.d(TAG,"event size1 = ${it.events?.size}")
                setJackpotInfo(it)
            }, {context?.also {con -> toast(setMessage(it, con))}})
        }


    }

    private fun setJackpotInfo(jackpotInfo: JackpotInfo) {
        jackpotInfo.coupon?.stakes?.also { stakes ->
            jackpot_stake_sp.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, stakes)

            jackpotInfo.coupon?.defaultStake?.also { defStake ->
                stake = defStake
                jackpot_stake_sp.setSelection(stakes.indexOfFirst { st -> st == defStake.toString() })
            }
        }

        val listEvents = mutableListOf<Event>()
        Log.d("eventss","jackpotInfo.events ${jackpotInfo.events?.keys}  jackpotInfo.altEvents ${jackpotInfo.altEvents?.keys}")
        Log.d("eventss","jackpotInfo.events ${jackpotInfo.events?.values?.map { it.id}} jackpotInfo.altEvents ${jackpotInfo.altEvents?.keys}")
        listEvents.addAll(jackpotInfo.events!!.values)

        jackpotInfo.altEvents?.also { events ->
            events.forEach { (t, u) ->
                listEvents.add(u.apply { isAltGame = true })
            }
        }

        gamesAdapter.setItems(listEvents)
        jackpotInfo.coupon?.also { coupon ->
            jackpot_coupon_id.text = coupon.coupon?.id.toString()

            val date = coupon.coupon?.expires?.toFullDate()!!.dateToString()
            jackpot_coupon_last_entry.text = date
        }
    }

    private fun setJackpotInfo( betLookup: BetLookupObj,jackpotInfo: JackpotInfo) {
        jackpotInfo.coupon?.stakes?.also { stakes ->
            jackpot_stake_sp.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, stakes)
                stake = betLookup.stake?.toInt()
                jackpot_stake_sp.setSelection(stakes.indexOfFirst { st -> st == stake.toString() })
        }
        val listEvents = mutableListOf<Event>()

        if(betLookup.events?.filter { it.bet.isNotEmpty() }?.size ==betLookup.events?.size){
            jackpot_create_ticket_btn.isEnabled = true
        }

        val events = mutableMapOf<String,Event>()
      betLookup.events?.forEachIndexed { index, event ->
          events[index.toString()] = event
      }
        jackpotInfo.events = events.toMap()
        val date = betLookup.updated?.date?.toFullDate2()!!.dateToString()
        jackpot_coupon_last_entry.text = date
        jackpot_coupon_id.text = jackpotInfo.coupon?.coupon?.id.toString()
        jackpotInfo.events?.values?.toMutableList()?.let { gamesAdapter.setItems(it) }

    }

    private fun sendRequest(eventsList: ArrayList<Pair<String, String>>, altEventsList: ArrayList<Pair<String, String>>) {

        subscribe(viewModel.jackpotAgentBetting(
            convertMapToList(eventsList),
            stake!!,
            convertMapToList(altEventsList)
        ), { result ->
            if (result.error_message.isEmpty()) {
                subscribe(viewModel.betLookup(result.message_data.betCode), {
                    subscribe(viewModel.checkTicket(result.message_data.betCode), {
                        viewModel.betsDetailsList.value = eventsList
                        showFragment(
                            JackpotConfirmationFragment.newInstance(),
                            R.id.container_for_fragments,
                            JackpotConfirmationFragment.TAG
                        )
                    }, {
                        context?.also {con -> toast(setMessage(it, con))}
                    })

                }, {
                    context?.also {con -> toast(setMessage(it, con))}
                })

            } else {
                toast(result.error_message)
            }

        }, {
            context?.also {con -> toast(setMessage(it, con))}
        })
    }

    private fun convertMapToList(listPair: ArrayList<Pair<String, String>>) : ArrayList<String> {
        val list = arrayListOf<String>()
        listPair.forEach { pair ->
            list.add(convertFieldToKey(pair.second))
        }
        return list
    }

    private fun convertFieldToKey(field: String): String {
        return when (field) {
            "Home"  -> "1"
            "Draw"  -> "X"
            else    -> "2"
        }
    }
}