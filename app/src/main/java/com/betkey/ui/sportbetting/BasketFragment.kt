package com.betkey.ui.sportbetting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.PrintObj
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.BetLookupObj
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.betkey.utils.*
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_basket.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class BasketFragment : BaseFragment() {

    companion object {
        const val TAG = "BasketFragment"

        fun newInstance() = BasketFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private var totalOdds: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sportbetting_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            place_bet_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (viewModel.lookupBets.value == null) {
                    placeBetAgent()
                } else {
                    placeBetPlayer()
                }
            }
        )
        compositeDisposable.add(
            clear_all_bets_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.basketList.value = mutableListOf()
                clearFields()
            }
        )
        compositeDisposable.add(
            back.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        amount_ET.addTextChangedListener(textWatcher)

        viewModel.basketList.observe(myLifecycleOwner, Observer { list ->
            list?.also { initAdapter(list) }
        })
        viewModel.sportBettingStatus.observe(myLifecycleOwner, Observer { status ->
            status?.also {
                toast("Place bet error $status")
                viewModel.sportBettingStatus.value = null
            }
        })
        viewModel.lookupBets.observe(myLifecycleOwner, Observer { lookupBets ->
            lookupBets?.also { lookup -> createBasketList(lookup) }
        })
        clearFields()
    }

    private fun placeBetPlayer() {
        subscribe(viewModel.getAgentProfile(amount_ET.text.toString()), { profile ->
        }, { context?.also { con -> toast(setMessage(it, con)) } })
    }

    private fun placeBetAgent() {
        if (!isLowBattery(context!!)){
            subscribe(viewModel.getAgentProfile(amount_ET.text.toString()), {
                //            toast("Success!!!!")
                viewModel.printObj.value = PrintObj(
                    totalOdds = total_odds.text.toString(),
                    bonus = bonus.text.toString(),
                    potentialWin = potential_win.text.toString(),
                    netWinning = payout.text.toString()
                )
                UsbPrinterActivity.start(activity!!, UsbPrinterActivity.SPORT_BETTING)
                activity?.finish()
            }, { context?.also { con -> toast(setMessage(it, con)) } })
        }
    }

    private fun initAdapter(list: MutableList<SportBetBasketModel>) {
        basket_adapter.adapter = BasketAdapter(list) { idEvent ->
            val newList = mutableListOf<SportBetBasketModel>()
            newList.addAll(list)
            list.forEach { modelInList ->
                if (modelInList.idEvent == idEvent) {
                    newList.remove(modelInList)
                }
            }
            viewModel.basketList.value = newList
        }
        calculateTotalOdds(list)
    }

    private fun createBasketList(bets: BetLookupObj) {
        bets.events?.also { list ->
            val basketList = mutableListOf<SportBetBasketModel>()
            for (i in list.indices) {
                val event = list[i]
                val teamsName = "${event.teams["1"]?.name} - ${event.teams["2"]?.name}"
                var date = ""
                event.time!!.date?.also { date = it.toFullDate2().dateToString3() }
                var betWinName = ""
                when (event.bet) {
                    "1" -> event.teams["1"]?.name?.also { name -> betWinName = name }
                    "X" -> betWinName = resources.getString(R.string.sportbetting_draw)
                    "2" -> event.teams["1"]?.name?.also { name -> betWinName = name }
                }
                basketList.add(
                    SportBetBasketModel(
                        idEvent = event.id!!,
                        league = event.league!!.name,
                        teamsName = teamsName,
                        date = date,
                        marketKey = event.market,
                        marketName = event.market_name,
                        betWinName = betWinName,
                        odds = event.odds.toString(),
                        betKey = event.bet,
                        lineName = event.line
                    )
                )
            }
            amount_ET.setText(bets.stake.toString())
            initAdapter(basketList)
        }
    }

    private fun clearFields() {
        viewModel.wallets.value?.get(0)?.currency?.also {
            val default = "0 $it"
            bonus.text = default
            potential_win.text = default
            total_win.text = default
            payout.text = default
        }
    }

    private fun calculateTotalOdds(list: MutableList<SportBetBasketModel>) {
        if (list.size == 0) {
            totalOdds = 0.0
            total_odds.text = "0"
            place_bet_btn.isEnabled = false
        } else {
            totalOdds = list.map { it.odds }
                .map { it.toDouble() }
                .reduce { acc, d -> acc.times(d) }
            val oddsText = totalOdds.roundOffDecimalComma()
            total_odds.text = oddsText
        }

        if (amount_ET.text.toString().isNotEmpty()) {
            fillAllFields(amount_ET.text.toString().toDouble())
        }
    }

    private fun fillAllFields(stake: Double) {
        viewModel.wallets.value?.get(0)?.currency?.also {
            val win = (totalOdds * stake).roundOffDecimalWithComma()
            val potentWin = "$win $it"
            potential_win.text = potentWin
            total_win.text = potentWin
            payout.text = potentWin
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            var isEnabledFlag = false
            viewModel.basketList.value?.size?.also {
                isEnabledFlag =
                    searchText.isNotEmpty() && searchText.toString().toDouble() > 0 && it > 0
            }

            viewModel.lookupBets.value?.also {
                isEnabledFlag = searchText.isNotEmpty() && searchText.toString().toDouble() > 0
            }

            place_bet_btn.isEnabled = isEnabledFlag
            if (searchText.isNotEmpty() && searchText.toString().toDouble() == 0.0) {
                amount_ET.setText("")
            }
            if (isEnabledFlag) {
                fillAllFields(searchText.toString().toDouble())
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.lookupBets.value = null
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        head_text.text = dictionary[Translation.Basket.TITLE]
        amount_title.text = dictionary[Translation.Basket.AMOUNT]
        total_odds_title.text = dictionary[Translation.Basket.TOTAL_ODDS]
        potential_win_title.text = dictionary[Translation.Basket.POTENTIAL_WIN]
        bonus_title.text = dictionary[Translation.Basket.BONUS]
        total_win_title.text = dictionary[Translation.Basket.TOTAL_WIN]
        payout_title.text = dictionary[Translation.Basket.PAYOUT]
        place_bet_btn.text = dictionary[Translation.PLACE_BET]
        clear_all_bets_btn.text = dictionary[Translation.Basket.CLEAR_ALL_BETS]
        back.text = dictionary[Translation.BACK]
    }
}