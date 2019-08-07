package com.betkey.ui.scanTickets

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
import com.betkey.ui.login.LoginOkFragment
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanTicketsActivity  : BaseActivity() {

    companion object {
        private const val SELECTED_ID = "id_selected"

        fun start(activity: Activity) {
            val intent = Intent(activity, ScanTicketsActivity::class.java).apply {
                //                putExtra(SELECTED_ID, idSelected)
            }
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_for_activity)

        showFragment(ScanFragment.newInstance(), R.id.container_for_fragments, ScanFragment.TAG)

        viewModel.wallets.observe(this, Observer { wallets ->
            wallets?.also {
                val text = "${String.format("%.2f", it[0].balance)} ${it[0].currency.toUpperCase()}"
                text_toolbar.text = text
            }
        })
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