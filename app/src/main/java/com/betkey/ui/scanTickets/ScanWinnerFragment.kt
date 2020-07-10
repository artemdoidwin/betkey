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
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_winner.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanWinnerFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanWinnerFragment"

        fun newInstance() = ScanWinnerFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_winner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            winner_payout_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                addFragment(
                    ScanPayoutSuccessFragment.newInstance(),
                    R.id.container_for_fragments,
                    ScanPayoutSuccessFragment.TAG
                )
            }
        )
        compositeDisposable.add(
            winner_ticket_detail_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.ticket.value?.also { ticket ->
                    subscribe(viewModel.betLookup(ticket), {
                        addFragment(
                            ScanTikcetDetailsFragment.newInstance(),
                            R.id.container_for_fragments,
                            ScanTikcetDetailsFragment.TAG
                        )
                    }, {
                        context?.also {con -> toast(setMessage(it, con))}
                    })
                }
            }
        )
        compositeDisposable.add(
            winner_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.link.value = null
                popBackStack()
            }
        )

        viewModel.ticket.observe(myLifecycleOwner, Observer { ticket ->
            ticket?.also {
                winner_created.text = dateString(it.created!!.toLong())
                winner_type.text = it.platformUnit!!.name
                winner_ticket_id.text = it.ticketId
                winner_sum.text = it.payout_amounts?.winning?.roundOffDecimal() ?: "0"
                winner_currency.text = it.currency
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.link.value = null
        viewModel.restartScan.call()
    }
}