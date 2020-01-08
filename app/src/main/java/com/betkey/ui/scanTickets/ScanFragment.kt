package com.betkey.ui.scanTickets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.betkey.utils.Translation
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_found_player.*
import kotlinx.android.synthetic.main.fragment_scan_tickets.*
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class ScanFragment : BaseFragment(), ZBarScannerView.ResultHandler {

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
        return inflater.inflate(R.layout.fragment_scan_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initScanner()

        compositeDisposable.add(
            light_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                mScannerView.flash = !mScannerView.flash
            }
        )
        compositeDisposable.add(
            scan_search_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                checkCode(code_ET.text.toString())
            }
        )
        compositeDisposable.add(
            scan_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                viewModel.link.value = null
                activity!!.finish()
            }
        )
        viewModel.link.observe(myLifecycleOwner, Observer { link ->
            link?.also { checkCode(it) }
        })
        viewModel.restartScan.observe(myLifecycleOwner, Observer {
            mScannerView.resumeCameraPreview(this)
        })
        code_ET.addTextChangedListener(textWatcher)
        scan_search_btn.isEnabled = false
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            scan_search_btn.isEnabled = searchText.isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    private fun checkCode(code: String) {
        subscribe(viewModel.checkTicket(code), {
            if (it.errors.isNotEmpty() && it.errors[0].code == 43) {
                showErrors(it.errors[0])
            } else {
                ticket(it.ticket)
            }
        }, {
            if (it.message == null) {
                toast(resources.getString(R.string.enter_password))
            } else {
                context?.also { con -> toast(setMessage(it, con)) }
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

    private fun initScanner() {
        mScannerView = ZBarScannerView(context)
        mScannerView.setFormats(listOf(BarcodeFormat.QRCODE))
        mScannerView.setResultHandler(this)
        previewLL.addView(mScannerView)
    }

    override fun onResume() {
        super.onResume()
        mScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        title.text = dictionary[Translation.Scan.TITLE]
        code_ET.hint = dictionary[Translation.Scan.ENTER_CODE_HINT]
        scan_back_btn.text = dictionary[Translation.Scan.BACK]
    }
}