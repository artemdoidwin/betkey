package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_winner.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanWinnerFragment : BaseFragment() {

    companion object {
        const val TAG = "ScanWinnerFragment"

        fun newInstance() = ScanWinnerFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_scan_winner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            winner_payout_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                addFragment(ScanPayoutSuccessFragment.newInstance(), R.id.container_for_fragments, ScanPayoutSuccessFragment.TAG)
            }
        )
        compositeDisposable.add(
            winner_ticket_detail_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                addFragment(ScanTikcetDetailsFragment.newInstance(), R.id.container_for_fragments, ScanTikcetDetailsFragment.TAG)
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