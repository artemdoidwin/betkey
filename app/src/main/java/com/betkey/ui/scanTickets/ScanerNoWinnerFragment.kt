package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.Ticket
import com.betkey.ui.MainViewModel
import com.betkey.utils.dateString
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_winner.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanerNoWinnerFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanerNoWinnerFragment"

        fun newInstance() = ScanerNoWinnerFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_winner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        winner_payout_btn.visibility = View.GONE
        winner_sum.visibility = View.GONE
        winner_currency.visibility = View.GONE

        viewModel.ticket.observe(myLifecycleOwner, Observer { ticket ->
            ticket?.also {
                createScreen(it)

                winner_created.text = dateString(it.created!!.toLong())
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
                    }, { context?.also {con -> toast(setMessage(it, con))} })
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

    private fun createScreen(ticket: Ticket) {
        when (ticket.outcome) {
            0 -> {
                winner_logo.setImageResource(R.drawable.ic_pending)
                winner_head_text.text = resources.getString(R.string.winner_head_pending)
                winner_head_text.textColor = ContextCompat.getColor(context!!, R.color.pending)
                return
            }// "open"
            2 -> {
                winner_logo.setImageResource(R.drawable.no_winner)
                winner_head_text.text = resources.getString(R.string.winner_head_no_winner)
                winner_head_text.textColor = ContextCompat.getColor(context!!, R.color.red)
                return
            }//"lost"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.link.value = null
        viewModel.restartScan.call()
    }
}