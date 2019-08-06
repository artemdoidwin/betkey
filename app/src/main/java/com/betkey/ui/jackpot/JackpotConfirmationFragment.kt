package com.betkey.ui.jackpot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jacpot_confirmation.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class JackpotConfirmationFragment : BaseFragment() {

    companion object {
        const val TAG = "JackpotConfirmationFragment"

        fun newInstance() = JackpotConfirmationFragment()
    }

    private lateinit var gamesAdapter: BetsAdapter
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_jacpot_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.also { it.text_toolbar.text = "rrr" }

        compositeDisposable.add(
            confirmation_home_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                Log.d("", "")
            }
        )

        viewModel.betsDetailsList.value?.also {
            gamesAdapter = BetsAdapter(it)
            bet_adapter.adapter = gamesAdapter
        }

        compositeDisposable.add(
            confirmation_home_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity?.also { it.finish() }
            }
        )
    }
}