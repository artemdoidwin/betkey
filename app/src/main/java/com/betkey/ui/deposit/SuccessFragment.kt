package com.betkey.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.betkey.utils.Translation
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_deposit_success.*
import org.jetbrains.anko.textColor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class SuccessFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "SuccessFragment"

        fun newInstance() = SuccessFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_deposit_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        compositeDisposable.add(
            deposit_success_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity!!.finish()
            }
        )

        viewModel.player.observe(this, Observer { player ->
            player?.also {
                val name = "${it.first_name} ${it.last_name}"
                deposit_success_name.text = name
                deposit_success_phone.text = it.phone
            }
        })

        viewModel.withdrawalConfirm.observe(this, Observer { withdrawal ->
            withdrawal?.confirm?.a2pDeposit?.payment?.also {
                val confirmSum = String.format("%.0f", it.amount.toDouble())
                deposit_success_sum.text = confirmSum
                deposit_success_currency.text = it.currency
                UsbPrinterActivity.start(activity!!, UsbPrinterActivity.WITHDRAWAL)
            }
        })

        viewModel.agentDeposit.observe(this, Observer { payment ->
            payment?.player_deposit?.payment?.also {
                val confirmSum = String.format("%.0f", it.amount.toDouble())
                deposit_success_sum.text = confirmSum
                deposit_success_currency.text = it.currency
                UsbPrinterActivity.start(activity!!, UsbPrinterActivity.DEPOSIT)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.withdrawalConfirm.value = null
        viewModel.agentDeposit.value = null
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        when (activity!!.localClassName) {
            "ui.withdrawal.WithdrawalActivity" -> {
                deposit_success_head_text.text = dictionary[Translation.Deposit.WITHDRAWAL_SUCCESS]
                deposit_success.text = dictionary[Translation.Deposit.WITHDRAWAL_IS_SUCCESSFUL]
                deposit_instantly.text = dictionary[Translation.Deposit.PLAY_PLAYER]
                deposit_instantly.textColor = ContextCompat.getColor(context!!, R.color.red)
                deposit_instantly.textSize = 22F
            }
        }
        deposit_success_name_title.text = dictionary[Translation.ConfirmDeposit.NAME]
        deposit_success_phone_title.text = dictionary[Translation.ConfirmDeposit.MOBILE_NUMBER]
        deposit_success_back_btn.text = dictionary[Translation.HOME]
    }
}