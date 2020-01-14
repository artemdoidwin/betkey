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
import com.betkey.utils.Translation
import com.betkey.utils.isLowBattery
import com.betkey.utils.setMessage
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

    //translation
    private var confirmWithdrawal = ""

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
            "com.betkey.ui.withdrawal.WithdrawalActivity" -> {
                deposit_confirm_head_text.text = confirmWithdrawal
                deposit_confirm_btn.text = confirmWithdrawal
                compositeDisposable.add(
                    deposit_confirm_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                        if (!isLowBattery(context!!)){
                            subscribe(viewModel.agentWithdrawal(code!!), {
                                if (checkErrors(it)) {
                                    updateWallets()
                                }
                            }, {context?.also {con -> toast(setMessage(it, con))}})
                        }
                    }
                )
            }
            "com.betkey.ui.deposit.DepositActivity" -> {
                compositeDisposable.add(
                    deposit_confirm_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                        if (!isLowBattery(context!!)){
                            subscribe(viewModel.agentDeposit(playerId, currency, sum!!.toInt()), {
                                updateWallets()
                            }, {context?.also {con -> toast(setMessage(it, con))}})
                        }
                    }
                )
            }
        }

        compositeDisposable.add(
            deposit_confirm_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        val confirmSum = String.format("%.0f", sum)
        deposit_confirm_sum.text = confirmSum
        viewModel.player.observe(this, Observer { player ->
            player?.also {updatePlayerData(it) }
        })
        viewModel.withdrawalRequest.observe(this, Observer { withdrawalRequest ->
            withdrawalRequest?.request?.also {
                val sum = String.format("%.0f", it.amount)
                deposit_confirm_sum.text = sum
                deposit_confirm_currency.text = it.currency
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
        }, {context?.also {con -> toast(setMessage(it, con))}})
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.withdrawalRequest.value = null
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        deposit_confirm_head_text.text = dictionary[Translation.ConfirmDeposit.TITLE]
        deposit_confirm_name_title.text = dictionary[Translation.ConfirmDeposit.NAME]
        deposit_confirm_phone_title.text = dictionary[Translation.ConfirmDeposit.MOBILE_NUMBER]
        deposit_confirm_btn.text = dictionary[Translation.ConfirmDeposit.CONFIRM_DEPOSIT]
        deposit_confirm_back_btn.text = dictionary[Translation.BACK]
        confirmWithdrawal = dictionary[Translation.ConfirmDeposit.CONFIRM_WITHDRAWAL] ?: context!!.resources.getString(R.string.withdrawal_confirm)
    }
}