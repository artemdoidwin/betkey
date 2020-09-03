package com.betkey.ui.sportbetting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.PrintObj
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.BetLookupObj
import com.betkey.network.models.BetLookupObj2
import com.betkey.network.models.Rule
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.betkey.utils.*
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_basket.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class BasketFragment(private val code: String? = null) : BaseFragment() {

    companion object {
        const val TAG = "BasketFragment"

        fun newInstance(code: String? = null) = BasketFragment(code)
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private var totalOdds: Double = 0.0
    private var tax: Double = 0.0
    private var salesTax = 0.0
    private var mBonus: Int = 0
    private var stakeWithTax: String = ""

    var rules : List<Rule>? = listOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sportbetting_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!code.isNullOrEmpty()){
            clearFields()
        }
            subscribe(viewModel.getPrematchBetting(),{
                salesTax = when (val salesTaxFromBackend = it.platform_unit.settings.sales_tax_value) {
                    7.0 -> salesTaxFromBackend/100
                    else -> salesTaxFromBackend
                }
                tax10_title.text = String.format(resources.getString(R.string.tax_10),(salesTax*100).toInt())
                Log.d("hgsiduhsid","jdoijf $it")
            },{
                tax10_title.text = String.format(resources.getString(R.string.tax_10),0)
                it.printStackTrace()
            })

            subscribe(viewModel.getInstances(),{
                rules = it.betslipBonuses[0].rules


                 it.tax?.let {taxNN->
                    tax =  taxNN
                     tax5_title.text = String.format(resources.getString(R.string.tax_s),(taxNN*100).toInt())
                     if (!code.isNullOrEmpty()){
                         viewModel.lookupBets2.observe(myLifecycleOwner, Observer { lookupBets ->
                             if(lookupBets.approved == true){
                                 Toast.makeText(this.context,resources.getString(R.string.already_approved),Toast.LENGTH_LONG).show()
                             }
                             lookupBets?.also { lookup -> createBasketList(lookup) }
                         })
                     }
                }


            },{
                tax5_title.text = String.format(resources.getString(R.string.tax_s),0)
                if (!code.isNullOrEmpty()){
                    viewModel.lookupBets2.observe(myLifecycleOwner, Observer { lookupBets ->
                        if(lookupBets.approved == true){
                            Toast.makeText(this.context,resources.getString(R.string.already_approved),Toast.LENGTH_LONG).show()
                        }
                        lookupBets?.also { lookup -> createBasketList(lookup) }
                    })
                }
                it.printStackTrace()
            })


        compositeDisposable.add(
            place_bet_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (code.isNullOrEmpty()){
                    if (viewModel.lookupBets.value == null) {
                        placeBetAgent()
                    }
                }else{
                    code?.let {

                        subscribe(  viewModel.approveBetlsip(it),{

                            viewModel.printObj.value = PrintObj(
                                stack = stakeTv.text.toString(),
                                totalOdds = total_odds.text.toString(),
                                bonus = bonus.text.toString(),
                                potentialWin = potential_win.text.toString(),
                                netWinning = payout.text.toString(),
                                incomeTax = tax5.text.toString(),
                                salesTax = tax10.text.toString(),
                                incomeTaxTitle = tax5_title.text.toString(),
                                salesTaxTitle = tax10_title.text.toString(),
                                totalWin = total_win.text.toString(),
                                stakeWithTax = stakeWithTax
                        )
                            UsbPrinterActivity.start(activity!!, UsbPrinterActivity.SPORT_BETTING)
                            activity?.finish()},
                            {
                                Log.d("Taganich"," error ${it.message}")
//                                toast(it.message.toString())
                            it.printStackTrace()}
                        )
                    }
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

        viewModel.basketList.observe(myLifecycleOwner, Observer { list  ->
            list?.also { initAdapter(list) }
        })
        viewModel.sportBettingStatus.observe(myLifecycleOwner, Observer { status ->
            status?.also {
                when(it) {
                    1 -> toast(getString(R.string.successful_result))
                    0 -> toast(getString(R.string.market_blocked))
                    2 -> toast(getString(R.string.odds_were_changed))
                    3 -> toast(getString(R.string.stake_is_too_big))
                    4 -> toast(getString(R.string.stake_is_too_low))
                    6 -> toast(getString(R.string.agent_dont_have_enough_money))
                    else -> toast(getString(R.string.something_went_wrong))
                }
                viewModel.sportBettingStatus.value = null
            }
        })


        if(code.isNullOrEmpty()){
            viewModel.lookupBets.observe(myLifecycleOwner, Observer { lookupBets ->
                lookupBets?.also { lookup -> createBasketList(lookup) }
            })
        }
        //amount_ET.isEnabled = code.isNullOrEmpty()

    }

    private fun placeBetPlayer() {
        subscribe(viewModel.getAgentProfile(stakeTv.text.toString()), { profile ->
        }, { context?.also { con -> toast(setMessage(it, con)) } })
    }

    private fun placeBetAgent() {
        if (!isLowBattery(context!!)){
            subscribe(viewModel.getAgentProfile(stakeTv.text.toString()), {
                //            toast("Success!!!!")
                viewModel.printObj.value = PrintObj(
                    stack = stakeTv.text.toString(),
                    totalOdds = total_odds.text.toString(),
                    bonus = bonus.text.toString(),
                    potentialWin = potential_win.text.toString(),
                    netWinning = payout.text.toString(),
                    incomeTax = tax5.text.toString(),
                    salesTax = tax10.text.toString(),
                    incomeTaxTitle = tax5_title.text.toString(),
                    salesTaxTitle = tax10_title.text.toString(),
                    totalWin = total_win.text.toString(),
                    stakeWithTax = stakeWithTax
                )
                UsbPrinterActivity.start(activity!!, UsbPrinterActivity.SPORT_BETTING)
                activity?.finish()
            }, {
//                context?.also { con -> toast(setMessage(it, con)) }
            it.printStackTrace()})
        }
    }

    private fun initAdapter(list: MutableList<SportBetBasketModel>) {
        basket_adapter.adapter = BasketAdapter(list, { idEvent ->
            val newList = mutableListOf<SportBetBasketModel>()
            newList.addAll(list)
            list.forEach { modelInList ->
                if (modelInList.idEvent == idEvent) {
                    newList.remove(modelInList)
                }
            }
            if (list.size == 0 ){
                    clearFields()
            }
            viewModel.basketList.value = newList
        },isCancelebla = code.isNullOrEmpty())
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
            initAdapter(basketList)
            amount_ET.setText(bets.stake.toString())
            fillAllFields(amount_ET.text.toString().toDouble())
            if(basketList.size == 0){
                clearFields()
            }



        }
    }
    private fun createBasketList(bets: BetLookupObj2) {
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
            if(basketList.size == 0){
                clearFields()
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
            tax5.text = default
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
            totalOdds = totalOdds.roundOffDecimalComma().toDouble()
            val oddsText = totalOdds.roundOffDecimalComma()
            total_odds.text = oddsText
        }

        if (amount_ET.text.toString().isNotEmpty()) {
            fillAllFields(amount_ET.text.toString().toDouble())
        }else{
            fillAllFields(0.0)
        }
    }

    private fun fillAllFields(ticketPrice: Double) {
        place_bet_btn.isEnabled = ticketPrice>0.0 && !(viewModel.lookupBets2.value?.approved?:false && !code.isNullOrEmpty())
        viewModel.wallets.value?.get(0)?.currency?.also {

            val rule =  rules?.find { it.number == basket_adapter?.adapter?.itemCount ?: 0 }
            Log.d("BONUS","betsBon $rule $rules")
            if (viewModel.basketList.value?.find { it.odds.toDouble() < rule?.bet ?: 0.0} == null){
                mBonus =rule?.bonus?.toInt() ?: 0
            }else{
               // mBonus = 0
            }
            val stake = ticketPrice-(ticketPrice*salesTax)
            val potentialWin = totalOdds * stake
            val tBonus = (potentialWin  - stake) * (mBonus.toDouble()/100)
            val totWin = potentialWin + tBonus
            val incomeTax = totWin*tax

            stakeTv.text = "${stake.roundOffDecimalWithComma()} $it"
            tax10.text = "${(ticketPrice*salesTax).roundOffDecimalWithComma()} $it"
            potential_win.text = "${potentialWin.roundOffDecimalWithComma()} $it"
            bonus.text = "${tBonus.roundOffDecimalWithComma()} $it"
            total_win.text = "${totWin.roundOffDecimalWithComma()} $it"//+bonuse
            tax5.text = "${incomeTax.roundOffDecimalWithComma()} $it"
            payout.text = "${(totWin-incomeTax).roundOffDecimalWithComma()} $it"
            stakeWithTax = "${(stake + ticketPrice*salesTax).roundOffDecimalWithComma()} $it"
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            var isEnabledFlag = searchText.isNotEmpty() && searchText.toString().toDouble() > 0 && basket_adapter?.adapter?.itemCount ?: 0 > 0 && !(viewModel.lookupBets2.value?.approved?:false && !code.isNullOrEmpty())




            if (searchText.isNullOrEmpty()){
                fillAllFields(0.0)
            }else{
                fillAllFields(searchText.toString().toDouble())
            }

            place_bet_btn.isEnabled = isEnabledFlag
            if (searchText.isNotEmpty() && searchText.toString().toDouble() == 0.0) {
                amount_ET.setText("")

            }

            if (isEnabledFlag) {

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