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
import kotlinx.android.synthetic.main.fragment_deposit_success.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class SuccessFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "SuccessFragment"

        fun newInstance() = SuccessFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        viewModel.payment.observe(this, Observer { payment ->
            payment?.also {paymentRest ->
                paymentRest.player_deposit?.also {playerDeposit ->
                    playerDeposit.payment?.also{
                        val confirmSum = String.format("%.2f", it.amount.toDouble())
                        deposit_success_sum.text = confirmSum
                        deposit_success_currency.text = it.currency
                    }
                }
            }
        })
    }
}