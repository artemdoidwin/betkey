package com.betkey.ui.deposit

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
import com.betkey.utils.Translation
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_found_player.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class FoundFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "FoundFragment"

        fun newInstance() = FoundFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_found_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            deposit_found_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (deposit_found_amount_ET.text.isNotEmpty()){
                    val sum = deposit_found_amount_ET.text.toString().toDouble()
                    if (sum > 0){
                        addFragment(ConfirmDepositFragment.newInstance(sum), R.id.container_for_fragments, ConfirmDepositFragment.TAG)
                    }
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
                deposit_found_currency.text = it.currency
            }
        })

        deposit_found_amount_ET.addTextChangedListener(textWatcher)
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        deposit_found_head_text.text = dictionary[Translation.FoundPlayer.TITLE]
        deposit_found_name_title.text = dictionary[Translation.FoundPlayer.NAME]
        deposit_found_phone_title.text = dictionary[Translation.FoundPlayer.MOBILE_NUMBER]
        deposit_found_amount_title.text = dictionary[Translation.Deposit.AMOUNT]
        deposit_found_btn.text = dictionary[Translation.Deposit.TITLE]
        deposit_found_back_btn.text = dictionary[Translation.BACK]
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            deposit_found_btn.isEnabled = searchText.isNotEmpty() && searchText.toString().toDouble() > 0
            if (searchText.isNotEmpty() && searchText.toString().toDouble() == 0.0){
                deposit_found_amount_ET.setText("")
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }
}