package com.betkey.ui.jackpot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.base.BaseFragment
import com.betkey.network.models.Bet
import com.betkey.network.models.Event
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jackpot.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.ArrayList


class JackpotFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "JackpotFragment"

        fun newInstance() = JackpotFragment()
    }

    private lateinit var gamesAdapter: JackpotGamesAdapter
    private lateinit var productsListener: GameListener
    private var betDetailsMap = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_jackpot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsListener = object : GameListener {
            override fun onCommandLeft(commandName: String, bet: Bet) {
                betDetailsMap[commandName] = bet.name
                if (betDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }

            override fun onIDraw(commandName: String, bet: Bet) {
                betDetailsMap[commandName] = bet.name
                if (betDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }

            override fun onCommandRight(commandName: String, bet: Bet) {
                betDetailsMap[commandName] = bet.name
                if (betDetailsMap.size == gamesAdapter.itemCount) {
                    jackpot_create_ticket_btn.isEnabled = true
                }
            }
        }
        gamesAdapter = JackpotGamesAdapter(productsListener)
        games_adapter.adapter = gamesAdapter

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

                viewModel.betsDetailsList.value = listPair
                showFragment(
                    JackpotConfirmationFragment.newInstance(),
                    com.betkey.R.id.container_for_fragments,
                    JackpotConfirmationFragment.TAG
                )
            }
        )

        subscribe(viewModel.getJacpotInfo(), {
            Log.d("", "")
            val li = mutableListOf<Event>()
            li.addAll(it.events!!.values)
            gamesAdapter.setItems(li)
        }, {
            toast(it.message.toString())
        })
    }
}