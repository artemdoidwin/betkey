package com.betkey.ui.jackpot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.*
import com.betkey.ui.MainViewModel
import com.betkey.utils.dateToString
import com.betkey.utils.setMessage
import com.betkey.utils.toFullDate2
import kotlinx.android.synthetic.main.fragment_jackpot_approve.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class JackpotApproveFragment(private val betLookup: BetLookupObj) : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var stake: Int? = null
    private lateinit var gamesAdapter: BetsAdapter
    companion object {
        const val TAG = "JackpotLookupFragment"

        fun newInstance(betLookup: BetLookupObj) = JackpotApproveFragment(betLookup)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jackpot_approve, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        subscribe(viewModel.getJacpotInfo(), {
            setJackpotInfo(betLookup,it)
        }, {context?.also {con -> toast(setMessage(it, con))}})
    }

    private fun setJackpotInfo( betLookup: BetLookupObj,jackpotInfo: JackpotInfo) {

        jackpot_approve_price_entry.text = betLookup.stake?.toInt().toString()

        if(betLookup.events?.filter { it.bet.isNotEmpty() }?.size ==betLookup.events?.size){
            jackpot_approve_ticket_btn.isEnabled = true
        }

        val events = mutableMapOf<String, Event>()
        betLookup.events?.forEachIndexed { index, event ->
            events[index.toString()] = event
        }
        jackpotInfo.events = events.toMap()
        val date = betLookup.updated?.date?.toFullDate2()!!.dateToString()
        jackpot_coupon_last_entry.text = date
        jackpot_coupon_id.text = jackpotInfo.coupon?.coupon?.id.toString()
        jackpotInfo.events?.values?.toMutableList()?.let {
            gamesAdapter = betLookup.events?.mapIndexed { i,v->
                Pair("${context?.resources?.getString(R.string.jackpot_game)} $i ${v.teams["1"]!!.name} - ${v.teams["2"]!!.name}",v.bet) }?.let {
                BetsAdapter(
                    it
                )
            }!!
            jackpot_approve_games_recycler.adapter = gamesAdapter}


        jackpot_approve_ticket_btn.setOnClickListener {
            viewModel.agentBet.postValue(AgentBettingResult(message_data = MessageData(stake = betLookup.stake!!.toInt(),betCode = betLookup.code)))
            subscribe(viewModel.checkTicket(betLookup.code), {
                betLookup.events?.mapIndexed { i,v->
                    Pair("${context?.resources?.getString(R.string.jackpot_game)} $i ${v.teams["1"]!!.name} - ${v.teams["2"]!!.name}",v.bet) }?.let {
                    viewModel.betsDetailsList.value = it
                }

            showFragment(
                JackpotConfirmationFragment.newInstance(),
                R.id.container_for_fragments,
                JackpotConfirmationFragment.TAG
            )
        }, {
            context?.also {con -> toast(setMessage(it, con))}
        })

        }
    }


}