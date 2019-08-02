package com.betkey.ui.jackpot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
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

        showFragment(JackpotFragment.newInstance(), R.id.container_for_fragments, JackpotFragment.TAG)
    }

    override fun onBackPressed() {
        val listFragments = supportFragmentManager.fragments.filter { frag -> frag.isVisible }
        val fragment = listFragments[listFragments.size - 1]
        if (fragment is JackpotFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}