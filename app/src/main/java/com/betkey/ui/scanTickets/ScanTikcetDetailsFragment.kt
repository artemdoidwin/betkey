package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.utils.Translation
import com.betkey.utils.dateString
import com.betkey.utils.roundOffDecimal
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_tikcet_detail.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanTikcetDetailsFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanTikcetDetailsFragment"

        fun newInstance() = ScanTikcetDetailsFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private var betsAdapter = BetAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_tikcet_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            scan_detail_home_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )

        viewModel.ticket.observe(myLifecycleOwner, Observer { ticket ->
            ticket?.also { t ->
                scan_detail_type.text = t.platformUnit!!.name
                t.stake?.also { stake ->
                    val price = "${stake.toDouble().roundOffDecimal()} ${t.currency}"
                    scan_detail_ticket_price.text = price
                }
                scan_detail_ticket_code.text = t.ticketId
                scan_detail_ticket_created.text = dateString(t.created!!.toLong())
                scan_detail_ticket_number.text = t.id
            }
        })

        viewModel.lookupBets.observe(myLifecycleOwner, Observer { lookupBets ->
            lookupBets?.also { bets ->
                bets.events?.also {
                    betsAdapter.setItems(it.toMutableList())
                    scan_detail_bet_adapter.adapter = betsAdapter
                }
            }
        })
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        betsAdapter.onTranslationReceived(dictionary)
        scan_detail_head_text.text = dictionary[Translation.ScanTicketsDetails.TICKET_DETAILS]
        scan_detail_type_title.text = dictionary[Translation.Ticket.TYPE]
        scan_detail_jackpot_title.text = dictionary[Translation.ScanTicketsDetails.JACKPOT]
        scan_detail_ticket_price_title.text = dictionary[Translation.ScanTicketsDetails.TICKET_PRICE]
        scan_detail_ticket_number_title.text = dictionary[Translation.ScanTicketsDetails.TICKET_NUMBER]
        scan_detail_ticket_code_title.text = dictionary[Translation.ScanTicketsDetails.TICKET_CODE]
        scan_detail_created_title.text = dictionary[Translation.ScanTicketsDetails.TICKET_CREATED]
        scan_detail_bet_details_title.text = dictionary[Translation.ScanTicketsDetails.BET_DETAILS]
        scan_detail_home_btn.text = dictionary[Translation.BACK]
    }
}