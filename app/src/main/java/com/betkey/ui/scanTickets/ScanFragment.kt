package com.betkey.ui.scanTickets

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_tickets.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit


class ScanFragment : BaseFragment(), QRCodeReaderView.OnQRCodeReadListener  {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "ScanFragment"
        const val QR_READER_CAMERA_REQUEST = 1004

        fun newInstance() = ScanFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_scan_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qr_decoder_view.setOnQRCodeReadListener(this)
        activity!!.text_toolbar.text = "rrr"

        compositeDisposable.add(
            winner_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity!!.finish()
            }
        )
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        text?.also { link ->
            toast(link)
//            Uri.parse(link)?.getQueryParameter(EventDetailsActivity.KEY_EVENT_ID)?.also { eventId ->
//                viewModel.openQREventId.value = eventId
//
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        qr_decoder_view.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qr_decoder_view.stopCamera()
    }
}