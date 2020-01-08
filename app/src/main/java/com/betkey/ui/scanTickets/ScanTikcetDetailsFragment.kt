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
import kotlinx.android.synthetic.main.fragment_scan_tikcet_detail.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanTikcetDetailsFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanTikcetDetailsFragment"

        fun newInstance() = ScanTikcetDetailsFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var betsAdapter: BetAdapter

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
                    betsAdapter = BetAdapter(it)
                    scan_detail_bet_adapter.adapter = betsAdapter
                }
            }
        })
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}