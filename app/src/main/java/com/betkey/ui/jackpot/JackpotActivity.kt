package com.betkey.ui.jackpot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class JackpotActivity : BaseActivity() {

    companion object {
        private const val SELECTED_ID = "id_selected"

        fun start(activity: Activity) {
            val intent = Intent(activity, JackpotActivity::class.java).apply {
                //                putExtra(SELECTED_ID, idSelected)
            }
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_for_activity)
        viewModel.wallets.value?.also {
            val text = "${String.format("%.2f", it[0].balance)} ${it[0].currency.toUpperCase()}"
            text_toolbar.text = text
        }

        showFragment(JackpotFragment.newInstance(), R.id.container_for_fragments, JackpotFragment.TAG)
    }

    override fun onBackPressed() {
        finish()
    }
}