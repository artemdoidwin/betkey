package com.betkey.ui.sportbetting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.BetLookupObj
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.betkey.utils.*
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_basket.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
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
        subscribe(viewModel.getAgentProfile(), { profile ->
            //            val agentId = profile.message?.agentDocument?.id!!
//            val username = profile.message?.agentDocument?.username!!
//            val created = Calendar.getInstance().time.dateToString4()
//
//
//            val i = (created).toMD5String()
//            val u = getMd5Base64((created))
//            val nonceMD5 = created.getMD5()
//            val passwordSHA1 = "$nonceMD5${created}U*LpMSk4y(pFw~Y=".getSHA1()
//            val passwordDigest = Base64.encodeToString(passwordSHA1.toByteArray(Charsets.UTF_8), 16)
//            val nonce = Base64.encodeToString(nonceMD5.toByteArray(Charsets.UTF_8), 16)
//            val idEvent = viewModel.lookupBets.value!!.id
//            val XWSSE =
//                "UsernameToken Username=\"$username\", PasswordDigest=\"$passwordDigest\", Nonce=\"$nonce\", Created=\"$created\""
        }, { toast(it.message.toString()) })

    }

    private fun placeBetAgent() {
        subscribe(viewModel.getAgentProfile(), { profile ->
            subscribe(
                viewModel.sprotBettingPlaceBet(
                    amount_ET.text.toString(),
                    profile.message?.agentDocument?.id!!
                ), {
                    it?.also {
                        subscribe(viewModel.checkTicket(it.code), { ticket ->
                            toast("Success!!!!")
                            UsbPrinterActivity.start(
                                activity!!,
                                UsbPrinterActivity.SPORTBETTING
                            )
                        }, { toast(it.message.toString()) })
                    }
                }, {
                    toast(it.message.toString())
                })
        }, { toast(it.message.toString()) })
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
            tax.text = default
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
}