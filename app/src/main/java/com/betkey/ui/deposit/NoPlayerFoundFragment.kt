package com.betkey.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.frafment_player_no_found.*
import java.util.concurrent.TimeUnit

class NoPlayerFoundFragment : BaseFragment() {

    companion object {
        const val TAG = "NoPlayerFoundFragment"

        fun newInstance() = NoPlayerFoundFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frafment_player_no_found, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            deposit_no_found_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
    }
}