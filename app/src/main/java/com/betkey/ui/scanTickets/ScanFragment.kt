package com.betkey.ui.scanTickets

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
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_tickets.*
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanFragment : BaseFragment(), ZBarScannerView.ResultHandler  {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var mScannerView: ZBarScannerView

    companion object {
        const val TAG = "ScanFragment"

        fun newInstance() = ScanFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("KKKKKK", "onCreateView")
        return inflater.inflate(R.layout.fragment_scan_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mScannerView = ZBarScannerView(context)
        mScannerView.setFormats(listOf(BarcodeFormat.QRCODE))
        previewLL.addView(mScannerView)
        Log.v("KKKKKK", "onViewCreated")

        compositeDisposable.add(
            scan_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.link.value = null
                activity!!.finish()
            }
        )
        viewModel.link.observe(myLifecycleOwner, Observer { link ->
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
        viewModel.restartScan.observe(myLifecycleOwner, Observer {
            mScannerView.resumeCameraPreview(this)
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
                    addFragment(
                        ScanerNoWinnerFragment.newInstance(),
                        R.id.container_for_fragments,
                        ScanerNoWinnerFragment.TAG
                    )
                    return
                }// "open"
                //won
                1 -> {
                    addFragment(
                        ScanWinnerFragment.newInstance(),
                        R.id.container_for_fragments,
                        ScanWinnerFragment.TAG
                    )
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
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                } // "payout"
                4 -> {
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                } // "cancelled"
                5 -> {
                    addFragment(
                        BlankOutcomeFragment.newInstance(viewModel.getOutcomes()?.get(it.outcome.toString())),
                        R.id.container_for_fragments, BlankOutcomeFragment.TAG
                    )
                    return
                } // "reverted"
            }
        }
    }

    override fun handleResult(rawResult: Result?) {
        Log.v("KKKKKK", "${rawResult?.contents} fragment")

        if (viewModel.link.value == null) {
            rawResult?.contents?.also {
                viewModel.link.postWithValue(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v("KKKKKK", "onResume")
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        Log.v("KKKKKK", "onPause")
        mScannerView.stopCamera()
    }
}