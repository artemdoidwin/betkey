package com.betkey.ui.scanTickets

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.ErrorObj
import com.betkey.network.models.Ticket
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.container_for_activity.*
import kotlinx.android.synthetic.main.fragment_scan_tickets.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit


class ScanFragment : BaseFragment(), QRCodeReaderView.OnQRCodeReadListener {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "ScanFragment"

        fun newInstance() = ScanFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_scan_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qr_decoder_view.setOnQRCodeReadListener(this)

        compositeDisposable.add(
            scan_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.link.value = null
                activity!!.finish()
            }
        )
        viewModel.link.observe(this, Observer { link ->
            link?.also { l ->
                subscribe(viewModel.checkTicket(l), {
                    if (it.errors.isNotEmpty() && it.errors[0].code == 43) {
                        showErrors(it.errors[0])
                    } else {
                        ticket(it.ticket)
                    }
                }, {
                    if (it.message == null) {
                        toast(resources.getString(R.string.enter_password))
                    } else {
                        toast(it.message.toString())
                    }
                })
            }
        })
    }

    private fun showErrors(errorObj: ErrorObj) {
        when (errorObj.code) {
            43 -> addFragment(
                ScanWrongTicketFragment.newInstance(),
                R.id.container_for_fragments,
                ScanWrongTicketFragment.TAG
            )
            else -> toast(errorObj.message.toString())
        }
    }

    private fun ticket(ticket: Ticket?) {
        Log.d("SCANS", "Ticket outcome: ${ticket?.outcome}")

        ticket?.also {
            when (it.outcome) {
                0 -> {
                    Log.d("", "")
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                }// "open"
                //won
                1 -> {
                    addFragment(ScanWinnerFragment.newInstance(), R.id.container_for_fragments, ScanWinnerFragment.TAG)
                    return
                }
                //"lost"
                2 -> {
                    addFragment(
                        ScanerNoWinnerFragment.newInstance(),
                        R.id.container_for_fragments,
                        ScanerNoWinnerFragment.TAG
                    )
                    return
                }
                3 -> {
                    Log.d("", "")
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                } // "payout"
                4 -> {
                    Log.d("", "")
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                } // "cancelled"
                5 -> {
                    Log.d("", "")
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                } // "reverted"
            }
            viewModel.link.value = null
        }
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        text?.also { link ->
            if (viewModel.link.value == null) {
                viewModel.link.value = link
                toast(link)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        qr_decoder_view.startCamera()
        qr_decoder_view.setAutofocusInterval(1000)
    }

    override fun onPause() {
        super.onPause()
        qr_decoder_view.stopCamera()
    }
}