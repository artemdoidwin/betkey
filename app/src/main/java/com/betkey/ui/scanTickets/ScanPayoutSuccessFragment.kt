package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.utils.Translation
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
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        scan_success_created_title.text = dictionary[Translation.Ticket.CREATED]
        scan_success_type_title.text = dictionary[Translation.Ticket.TYPE]
        scan_success_ticket_id_title.text = dictionary[Translation.Ticket.TICKET_ID]
        scan_success_play_player.text = dictionary[Translation.ScanSuccess.PLAY_PLAYER]
        scan_success_back_btn.text = dictionary[Translation.HOME]
    }
}