package com.betkey.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.AgentWithdrawal
import com.betkey.network.models.Player
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
        private const val CODE = "code"

        fun newInstance(sum: Double) = ConfirmDepositFragment().apply {
            arguments = Bundle().apply {
                putDouble(SUMM_AMOUNT, sum)
            }
        }

        fun newInstance(code: Int) = ConfirmDepositFragment().apply {
            arguments = Bundle().apply {
                putInt(CODE, code)
            }
        }
    }

    private val sum by lazy { arguments?.getDouble(SUMM_AMOUNT) }
    private val code by lazy { arguments?.getInt(CODE) }
    private var playerId: String = ""
    private var currency: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm_deposite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activity!!.localClassName) {
            "ui.withdrawal.WithdrawalActivity" -> {
                deposit_confirm_head_text.text =
                    context!!.resources.getString(R.string.withdrawal_confirm)
                deposit_confirm_btn.text =
                    context!!.resources.getString(R.string.withdrawal_confirm)
                compositeDisposable.add(
                    deposit_confirm_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                        subscribe(viewModel.agentWithdrawal(code!!), {}, {
                            toast(it.message.toString())
                        })
                    }
                )
            }
            "ui.deposit.DepositActivity" -> {
                compositeDisposable.add(
                    deposit_confirm_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                        subscribe(viewModel.agentDeposit(playerId, currency, sum!!.toInt()), {
                            updateWallets()
                        }, {
                            toast(it.message.toString())
                        })
                    }
                )
            }
        }

        compositeDisposable.add(
            deposit_confirm_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        val confirmSum = String.format("%.2f", sum)
        deposit_confirm_sum.text = confirmSum
        viewModel.player.observe(this, Observer { player ->
            player?.also {updatePlayerData(it) }
        })
        viewModel.withdrawal.observe(this, Observer { withdrawal ->
            withdrawal?.also {
                if (checkErrors(it)) {
                    updateWallets()
                }else{
                    viewModel.withdrawal.value = null
                }
            }
        })
    }

    private fun updatePlayerData(player: Player) {
        playerId = player.id!!
        val name = "${player.first_name} ${player.last_name}"
        deposit_confirm_name.text = name
        deposit_confirm_phone.text = player.phone
        deposit_confirm_currency.text = player.currency
        currency = player.currency
    }

    private fun checkErrors(withdrawal: AgentWithdrawal): Boolean {
        return if (withdrawal.errors.isNotEmpty()) {
            when (withdrawal.errors[0].code) {
                91 -> toast(context!!.resources.getString(R.string.withdrawal_error_pin))
                else -> toast(withdrawal.errors[0].message!!)
            }
            false
        } else {
            true
        }
    }

    private fun updateWallets() {
        hideLoading()

        subscribe(viewModel.getAgentWallets(), {
            clearStack()
            addFragment(
                SuccessFragment.newInstance(),
                R.id.container_for_fragments,
                SuccessFragment.TAG
            )
        }, {
            toast(it.message.toString())
        })
    }
}