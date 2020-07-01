package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.utils.dateString
import com.betkey.utils.roundOffDecimal
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_scan_payout_success.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanPayoutSuccessFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanPayoutSuccessFragment"

        fun newInstance() = ScanPayoutSuccessFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_payout_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        compositeDisposable.add(
            scan_success_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
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
                it.message?.payout?.also { payout ->
                    val price = "${payout.roundOffDecimal()} ${t.currency}"
                    scan_success_sum.text = price
                }
                if(it.errorMessage.isNullOrEmpty()) {

                    scan_success_logo.visibility = View.VISIBLE
                    scan_success_logo_head_text.visibility = View.VISIBLE
                } else {
                    scan_success_logo.setImageResource(R.drawable.ic_close)
                    scan_success_logo_head_text.text = it.errorMessage
                    scan_success_logo.visibility = View.VISIBLE
                    scan_success_logo_head_text.visibility = View.VISIBLE
                }
            } , {
                scan_success_logo.setImageResource(R.drawable.ic_close)
                scan_success_logo_head_text.text = getString(R.string.payment_unsuccesful)
                scan_success_logo.visibility = View.VISIBLE
                scan_success_logo_head_text.visibility = View.VISIBLE
            })
        }
    }
}