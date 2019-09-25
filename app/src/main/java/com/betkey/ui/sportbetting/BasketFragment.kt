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
import com.betkey.models.SportBetBasketModel
import com.betkey.ui.MainViewModel
import com.betkey.utils.roundOffDecimal
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_basket.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class BasketFragment : BaseFragment() {

    companion object {
        const val TAG = "BasketFragment"

        fun newInstance() = BasketFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

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
        })
        clearFields()
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
        if (list.size == 0){
            total_odds.text = "0"
            place_bet_btn.isEnabled = false
        }else{
            total_odds.text = list.map { it.odds }
                .map { it.toDouble() }
                .reduce { acc, d ->
                    acc.times(d)
                }.roundOffDecimal()
        }

        if (amount_ET.text.toString().isNotEmpty()){
            fillAllFields(amount_ET.text.toString().toDouble())
        }
    }

    private fun fillAllFields(stake: Double){
        val win = total_odds.text.toString().toDouble() * stake
        viewModel.wallets.value?.get(0)?.currency?.also {
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
                isEnabledFlag = searchText.isNotEmpty() && searchText.toString().toDouble() > 0 && it > 0
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
}