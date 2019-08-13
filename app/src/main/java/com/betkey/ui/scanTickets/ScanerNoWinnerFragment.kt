package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.jackpot.JackpotConfirmationFragment
import com.betkey.utils.createDateString
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_winner.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanerNoWinnerFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanerNoWinnerFragment"

        fun newInstance() = ScanerNoWinnerFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_scan_winner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        winner_logo.setImageResource(R.drawable.no_winner)
        winner_payout_btn.visibility = View.GONE
        winner_sum.visibility = View.GONE
        winner_currency.visibility = View.GONE

        viewModel.ticket.observe(myLifecycleOwner, Observer { ticket ->
            ticket?.also {
                winner_created.text = createDateString(it.created!!.toLong())
                winner_type.text = it.platformUnit!!.name
                winner_ticket_id.text = it.ticketId
            }
        })

        compositeDisposable.add(
            winner_ticket_detail_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.ticket.value?.also { ticket ->

                    subscribe(viewModel.betLookup(ticket.ticketId!!), {
                        addFragment(
                            ScanTikcetDetailsFragment.newInstance(),
                            R.id.container_for_fragments,
                            ScanTikcetDetailsFragment.TAG
                        )
                    }, {
                        toast(it.message.toString())
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.link.value = null
    }
}