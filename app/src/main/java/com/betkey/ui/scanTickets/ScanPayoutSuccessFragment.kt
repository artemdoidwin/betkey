package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.PayoutModel
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.betkey.ui.login.LoginFragment
import com.betkey.ui.login.LoginOkFragment
import com.betkey.utils.dateString
import com.betkey.utils.roundOffDecimal
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_payout_success.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanPayoutSuccessFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanPayoutSuccessFragment"

        fun newInstance() = ScanPayoutSuccessFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_payout_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        compositeDisposable.add(
            scan_success_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                clearStack()
                showFragment(LoginOkFragment.newInstance(), R.id.container_for_fragments, LoginFragment.TAG)
            }
        )

        viewModel.ticket.observe(myLifecycleOwner, Observer { ticket ->
            ticket?.also { t ->
                scan_success_type.text = t.platformUnit!!.name
                scan_success_created.text = dateString(t.created!!.toLong())
                scan_success_ticket_id.text = t.ticketId
            }
        })

        viewModel.ticket.value?.also { t ->
            subscribe(viewModel.payoutTicket(t.ticketId!!), {

                subscribe(viewModel.getPrematchBetting(),{ prebet ->

                    val totalOdds = it.message?.totalOdds!!
                    val stake = it.message.stake!!
                    val potentialWin = totalOdds * stake

                    val salesTax = (prebet.platform_unit.settings.sales_tax_value * 100).toInt()
                    val salesTaxAmount = (prebet.platform_unit.settings.sales_tax_value * stake)

//                    val bonus = (potentialWin  - stake) * (bonus/100)

                    val tBonus = it.message.bonus?.bonus!!
                    val totWin = potentialWin + tBonus

                    val tax = it.message.tax!!
                    val incomeTax = (totWin -stake)*tax


                    viewModel.payoutModel.value = PayoutModel(
                        placeStake = it.message.stake.roundOffDecimal().toString(),
                        ticketNumber =  it.message.id!!,
                        ticketCode = it.message.code!!,
                        date = it.message.created!!.replace('T', ' ').replace("+00:00", ""),
                        type = t.platformUnit.name,
                        place = "${it.message.events?.size!!}/${it.message.events.size}",
                        totalOdds = it.message.totalOdds.toString(),
                        stake = (it.message.stake - salesTaxAmount).roundOffDecimal().toString(),
                        salesTax = "${salesTax}% ${salesTaxAmount.roundOffDecimal()} ${t.currency}",
                        potentialWin = potentialWin.roundOffDecimal().toString(),
                        currency = t.currency,
                        bonus = "${tBonus.roundOffDecimal()} ${t.currency}",
                        totalWin = "${totWin.roundOffDecimal()} ${t.currency}",
                        incomeTax = "${it.message.tax} ${t.currency}" ,
                        payout = it.message.payout!!.toString()
                    )

                    it.message.payout.also { payout ->
                        val price = "${payout.roundOffDecimal()} ${t.currency}"
                        scan_success_sum.text = price
                    }
                    if (it.errorMessage.isNullOrEmpty()) {

                        scan_success_logo.visibility = View.VISIBLE
                        scan_success_logo_head_text.visibility = View.VISIBLE
                    } else {
                        scan_success_logo.setImageResource(R.drawable.ic_close)
                        scan_success_logo_head_text.text = it.errorMessage
                        scan_success_logo.visibility = View.VISIBLE
                        scan_success_logo_head_text.visibility = View.VISIBLE
                    }

                    UsbPrinterActivity.start(activity!!, UsbPrinterActivity.PAYOUT_TICKET)

                },{
                    scan_success_logo.setImageResource(R.drawable.ic_close)
                    scan_success_logo_head_text.text = getString(R.string.payment_unsuccesful)
                    scan_success_logo.visibility = View.VISIBLE
                    scan_success_logo_head_text.visibility = View.VISIBLE
                })
            }, {
                scan_success_logo.setImageResource(R.drawable.ic_close)
                scan_success_logo_head_text.text = getString(R.string.payment_unsuccesful)
                scan_success_logo.visibility = View.VISIBLE
                scan_success_logo_head_text.visibility = View.VISIBLE
            })
        }
    }
}