package com.betkey.ui.scanTickets

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
import com.betkey.ui.login.LoginOkFragment
import com.betkey.ui.scanTickets.scaner.BarcodeGraphicTracker
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.android.synthetic.main.view_toolbar.*
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanTicketsActivity : BaseActivity(), BarcodeGraphicTracker.BarcodeUpdateListener {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, ScanTicketsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_for_activity)

        addFragment(ScanFragment.newInstance(), R.id.container_for_fragments, ScanFragment.TAG)

        viewModel.wallets.observe(this, Observer { wallets ->
            wallets?.also {
                val text = "${String.format("%.2f", it[0].balance)} ${it[0].currency.toUpperCase()}"
                text_toolbar.text = text
            }
        })
    }

    override fun onBarcodeDetected(barcode: Barcode?) {
        barcode?.displayValue?.also { link ->
            if (viewModel.link.value == null) {
                viewModel.link.postWithValue(link)
            }
        }
    }

    override fun onBackPressed() {
        val listFragments = supportFragmentManager.fragments.filter { frag -> frag.isVisible }
        val fragment = listFragments[listFragments.size - 1]
        if (fragment is LoginOkFragment) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}