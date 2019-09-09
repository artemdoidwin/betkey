package com.betkey.ui.scanTickets

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.ErrorObj
import com.betkey.network.models.Ticket
import com.betkey.ui.MainViewModel
import com.betkey.ui.scanTickets.scaner.BarcodeTrackerFactory
import com.betkey.ui.scanTickets.scaner.CameraSource
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_scan_tickets.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.IOException
import java.util.concurrent.TimeUnit

class ScanFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var mCameraSource: CameraSource? = null

    companion object {
        const val TAG = "ScanFragment"
        // intent request code to handle updating play services if needed.
        private val RC_HANDLE_GMS = 9001

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

        createCameraSource(true, useFlash = false)
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

    @SuppressLint("InlinedApi")
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        val barcodeDetector = BarcodeDetector.Builder(context).build()
        val barcodeFactory = BarcodeTrackerFactory(context!!)
        barcodeDetector.setProcessor(
            MultiProcessor.Builder(barcodeFactory).build()
        )

        if (!barcodeDetector.isOperational) {
            Log.w(TAG, "Detector dependencies are not yet available.")

            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = activity!!.registerReceiver(null, lowstorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(context, R.string.low_storage_error, Toast.LENGTH_LONG).show()
                Log.w(TAG, getString(R.string.low_storage_error))
            }
        }

        var builder: CameraSource.Builder = CameraSource.Builder(context, barcodeDetector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1600, 1024)
            .setRequestedFps(15.0f)

        builder = builder.setFocusMode(
            if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE else null
        )

        mCameraSource = builder
            .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else null)
            .build()
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
        }
    }

    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (code != ConnectionResult.SUCCESS) {
            val dlg =
                GoogleApiAvailability.getInstance().getErrorDialog(activity!!, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                preview.start(mCameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        preview.release()
    }
}