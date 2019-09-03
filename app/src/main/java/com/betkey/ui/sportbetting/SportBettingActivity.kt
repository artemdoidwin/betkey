package com.betkey.ui.sportbetting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
import com.betkey.ui.deposit.DepositActivity
import com.betkey.ui.deposit.FindPlayerFragment
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SportBettingActivity  : BaseActivity() {

    companion object {
        private const val SELECTED_ID = "id_selected"

        fun start(activity: Activity) {
            val intent = Intent(activity, SportBettingActivity::class.java).apply {
                //                putExtra(SELECTED_ID, idSelected)
            }
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_for_activity)

        addFragment(SportbetingFragment.newInstance(), R.id.container_for_fragments, SportbetingFragment.TAG)

        viewModel.wallets.observe(this, Observer { wallets ->
            wallets?.also {
                val nameToDisplay = when {
                    viewModel.agent.value?.firstName?.isNotEmpty() == true -> "${viewModel.agent.value?.firstName} - "
                    viewModel.agent.value?.username?.isNotEmpty() == true -> "${viewModel.agent.value?.username} - "
                    else -> ""
                }
                val text = "$nameToDisplay${String.format("%.2f", it[0].balance)} ${it[0].currency.toUpperCase()}"
                text_toolbar.text = text
            }
        })
    }

    override fun onBackPressed() {
//        val listFragments = supportFragmentManager.fragments.filter { frag -> frag.isVisible }
//        val fragment = listFragments[listFragments.size - 1]
//        if (fragment is FindPlayerFragment) {
//            finish()
//        } else {
            super.onBackPressed()
//        }
    }
}