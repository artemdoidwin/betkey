package com.betkey.ui.login

import android.os.Bundle
import com.betkey.R
import com.betkey.base.BaseActivity
import com.betkey.ui.MainViewModel
import com.betkey.ui.login.MainMenuFragment.Companion.RESTART_ACTIVITY
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_for_activity)

        val isActivityReloaded = intent.extras?.getBoolean(RESTART_ACTIVITY)
        if(isActivityReloaded == true) {
            //if language was changed
            showFragment(MainMenuFragment.newInstance(), R.id.container_for_fragments, LoginFragment.TAG)
        } else {
            showFragment(LoginFragment.newInstance(), R.id.container_for_fragments, LoginFragment.TAG)
        }
    }

    override fun onBackPressed() {
        val listFragments = supportFragmentManager.fragments.filter { frag -> frag.isVisible }
        val fragment = listFragments[listFragments.size - 1]
        if (fragment is MainMenuFragment) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}