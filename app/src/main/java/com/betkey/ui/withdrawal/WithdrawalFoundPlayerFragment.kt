package com.betkey.ui.withdrawal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.deposit.ConfirmDepositFragment
import com.betkey.ui.deposit.SuccessFragment
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_found_player.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class WithdrawalFoundPlayerFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "WithdrawalFoundPlayerFragment"

        fun newInstance() = WithdrawalFoundPlayerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_found_player_withdrawal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deposit_found_amount_ET.addTextChangedListener(textWatcherWithdrawal)

        compositeDisposable.add(
            deposit_found_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (deposit_found_amount_ET.text.isNotEmpty()) {
                    addFragment(
                        ConfirmDepositFragment.newInstance(deposit_found_amount_ET.text.toString().toInt()),
                        R.id.container_for_fragments,
                        ConfirmDepositFragment.TAG
                    )
                }
            }
        )

        compositeDisposable.add(
            deposit_found_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )

        viewModel.player.observe(this, Observer { player ->
            player?.also {
                val name = "${it.first_name} ${it.last_name}"
                deposit_found_name.text = name
                deposit_found_phone.text = it.phone
                deposit_found_currency?.also { currency -> currency.text = it.currency }
            }
        })
    }

    private val textWatcherWithdrawal = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            deposit_found_btn.isEnabled = searchText.isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }
}