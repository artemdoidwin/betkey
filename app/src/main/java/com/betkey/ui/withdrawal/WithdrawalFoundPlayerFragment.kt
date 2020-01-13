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
import com.betkey.network.models.WithdrawalRequest
import com.betkey.ui.MainViewModel
import com.betkey.ui.deposit.ConfirmDepositFragment
import com.betkey.utils.Translation
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_found_player_withdrawal.*
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

        withdrawal_found_amount_ET.addTextChangedListener(textWatcherWithdrawal)

        compositeDisposable.add(
            withdrawal_found_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (withdrawal_found_amount_ET.text.isNotEmpty()) {
                    subscribe(
                        viewModel.agentWithdrawalRequest(withdrawal_found_amount_ET.text.toString()),
                        {
                            checkErrors(it)
                        },
                        {
                            context?.also { con -> toast(setMessage(it, con)) }
                        }
                    )
                }
            }
        )

        compositeDisposable.add(
            withdrawal_found_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )

        viewModel.player.observe(this, Observer { player ->
            player?.also {
                val name = "${it.first_name} ${it.last_name}"
                withdrawal_found_name.text = name
                withdrawal_found_phone.text = it.phone
            }
        })
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        withdrawal_found_head_text.text = dictionary[Translation.FoundPlayer.TITLE]
        withdrawal_found_name_title.text = dictionary[Translation.FoundPlayer.NAME]
        withdrawal_found_phone_title.text = dictionary[Translation.FoundPlayer.MOBILE_NUMBER]
        withdrawal_found_amount_title.text = dictionary[Translation.FoundPlayer.WITHDRAWAL_PIN]
        withdrawal_found_btn.text = dictionary[Translation.FoundPlayer.WITHDRAWAL_FOUNDS]
        withdrawal_found_back_btn.text = dictionary[Translation.BACK]
    }

    private fun checkErrors(request: WithdrawalRequest) {
        if (request.errors.isNotEmpty()) {
            when (request.errors[0].code) {
                925 -> toast(context!!.resources.getString(R.string.withdrawal_error_pin))
                else -> toast(request.errors[0].message.toString())
            }
        } else {
            addFragment(
                ConfirmDepositFragment.newInstance(withdrawal_found_amount_ET.text.toString().toInt()),
                R.id.container_for_fragments,
                ConfirmDepositFragment.TAG
            )
        }
    }

    private val textWatcherWithdrawal = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            withdrawal_found_btn.isEnabled = searchText.isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }
}