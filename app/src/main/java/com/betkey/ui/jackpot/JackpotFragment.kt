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
import com.betkey.network.models.Event
import com.betkey.ui.MainViewModel
import com.betkey.utils.dateToString
import com.betkey.utils.toFullDate
import com.google.gson.Gson
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jackpot.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class JackpotFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "JackpotFragment"

        fun newInstance() = JackpotFragment()
    }

    private lateinit var gamesAdapter: JackpotGamesAdapter
    private lateinit var productsListener: GameListener
    private var betDetailsMap = HashMap<String, String>()
    private var stake: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jackpot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsListener = object : GameListener {
            override fun onCommandLeft(commandName: String, bet: Bet, selection: String) {
                betDetailsMap[commandName] = bet.name
                if (betDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }

            override fun onIDraw(commandName: String, bet: Bet, selection: String) {
                betDetailsMap[commandName] = bet.name
                if (betDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }

            override fun onCommandRight(commandName: String, bet: Bet, selection: String) {
                betDetailsMap[commandName] = bet.name
                if (betDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }
        }
        gamesAdapter = JackpotGamesAdapter(productsListener)
        jackpot_games_adapter.adapter = gamesAdapter

        compositeDisposable.add(
            jackpot_create_ticket_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {

                val listPair = betDetailsMap.toList() as ArrayList<Pair<String, String>>
                listPair.sortWith(Comparator { o1, o2 ->
                    when {
                        o1.first > o2.first -> 1
                        o1.first == o2.first -> 0
                        else -> -1
                    }
                })
                listPair.forEach {
                    Log.d("PAIRS", "First: ${it.first}, second: ${it.second}")
                }

                stake = jackpot_stake_sp.selectedItem.toString().toInt()
                sendRequest(listPair)
            }
        )

        subscribe(viewModel.getJacpotInfo(), {
            it.coupon?.stakes?.also { stakes ->
                jackpot_stake_sp.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, stakes)

                it.coupon?.defaultStake?.also { defStake ->
                    stake = defStake
                    jackpot_stake_sp.setSelection(stakes.indexOfFirst { st -> st == defStake.toString() })
                }
            }

            val li = mutableListOf<Event>()
            li.addAll(it.events!!.values)

            it.altEvents?.also { event -> li.add(event.apply { isAltGame = true }) }

            gamesAdapter.setItems(li)
            it.coupon?.also { coupon ->
                jackpot_coupon_id.text = coupon.coupon?.id.toString()

                viewModel.wallets.value?.also {
                    val price = "${coupon.defaultStake} ${viewModel.wallets.value!![0].currency} "
                    jackpot_ticket_price.text = price
                }

                val date = coupon.coupon?.expires?.toFullDate()!!.dateToString()
                jackpot_coupon_last_entry.text = date
            }
        }, {
            toast(it.message.toString())
        })
    }

    private fun sendRequest(listPair: ArrayList<Pair<String, String>>) {
        subscribe(viewModel.jackpotAgentBetting(
            convertFieldToKey(listPair[0].second),
            convertFieldToKey(listPair[1].second),
            convertFieldToKey(listPair[2].second),
            convertFieldToKey(listPair[3].second),
            convertFieldToKey(listPair[4].second),
            convertFieldToKey(listPair[5].second),
            convertFieldToKey(listPair[6].second),
            stake!!,
            2
        ), { result ->
            Log.d("PAIRS", "Result: ${Gson().toJson(result)}")
            Log.d("PAIRS", "Date: ${Date((result.created ?: 0) * 1000).dateToString()}")
            if (result.error_message.isEmpty()) {
                subscribe(viewModel.betLookup(result.message_data!!.betCode), {
                    subscribe(viewModel.checkTicket(result.message_data!!.betCode), {
                        viewModel.betsDetailsList.value = listPair
                        showFragment(
                            JackpotConfirmationFragment.newInstance(),
                            R.id.container_for_fragments,
                            JackpotConfirmationFragment.TAG
                        )
                    }, {
                        toast(it.message.toString())
                    })

                }, {
                    toast(it.message.toString())
                })

            } else {
                toast(result.error_message)
            }

        }, {
            toast(it.message.toString())
        })
    }

    private fun convertFieldToKey(field: String): String {
        var key = ""
        when (field) {
            "Home" -> key = "1"
            "Draw" -> key = "X"
            "Away" -> key = "2"
        }
        return key
    }
}