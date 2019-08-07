package com.betkey.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_confirm_deposite.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ConfirmDepositFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "ConfirmDepositFragment"
        private const val SUMM_AMOUNT = "summ_amount"

        fun newInstance(sum: Double) = ConfirmDepositFragment().apply {
            arguments = Bundle().apply {
                putDouble(SUMM_AMOUNT, sum)
            }
        }
    }

    private val sum by lazy {
        arguments?.getDouble(SUMM_AMOUNT)
    }
    private var playerId: String = ""
    private var currency: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirm_deposite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            deposit_confirm_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe(viewModel.agentDeposit(playerId, currency, sum!!.toInt()), {
                    hideLoading()

                    //update wallets
                    subscribe(viewModel.getAgentWallets(), {
                        addFragment(SuccessFragment.newInstance(), R.id.container_for_fragments, SuccessFragment.TAG)
                    },{
                        toast(it.message.toString())
                    })

                },{
                    toast(it.message.toString())
                })
            }
        )

        compositeDisposable.add(
            deposit_confirm_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        val confirmSum = String.format("%.2f", sum)
        deposit_confirm_sum.text = confirmSum
        viewModel.player.observe(this, Observer { player ->
            player?.also {
                playerId = it.id!!
                val name = "${it.first_name} ${it.last_name}"
                deposit_confirm_name.text = name
                deposit_confirm_phone.text = it.phone
                deposit_confirm_currency.text = it.currency
                currency = it.currency
            }
        })
    }
}