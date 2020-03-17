package com.betkey.ui.report

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
import com.betkey.ui.login.LoginFragment
import com.betkey.ui.login.LoginOkFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportActivity:BaseActivity() {
    private val viewModel by viewModel<MainViewModel>()
    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, ReportActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_for_activity)

            showFragment(CreateReportFragment.newInstance(), R.id.container_for_fragments, LoginFragment.TAG)

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