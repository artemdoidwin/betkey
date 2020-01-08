package com.betkey.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.withdrawal.WithdrawalFoundPlayerFragment
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_find_player.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class FindPlayerFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "FindPlayerFragment"

        fun newInstance() = FindPlayerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_find_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deposit_find_amount_ET.hint = getString(R.string.deposit_phone_number_hint, viewModel.phoneNumberCountryCode)

        compositeDisposable.add(
            deposit_find_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe(
                    viewModel.findPlayer(deposit_find_amount_ET.text.toString()), {
//                                                viewModel.findPlayer("237699000004"), {
                        if (it.errors.isNotEmpty() && it.errors[0].code == 33) {
                            addFragment(
                                NoPlayerFoundFragment.newInstance(),
                                R.id.container_for_fragments,
                                NoPlayerFoundFragment.TAG
                            )
                        } else {
                            checkFragment()
                        }
                    }, {
                        context?.also {con -> toast(setMessage(it, con))}
                    }
                )
            }
        )

        compositeDisposable.add(
            deposit_find_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity?.also { it.finish() }
            }
        )
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkFragment() {
        when (activity!!.localClassName) {
            "com.betkey.ui.withdrawal.WithdrawalActivity" -> {
                addFragment(
                    WithdrawalFoundPlayerFragment.newInstance(),
                    R.id.container_for_fragments,
                    WithdrawalFoundPlayerFragment.TAG
                )
            }
            "com.betkey.ui.deposit.DepositActivity" -> {
                addFragment(
                    FoundFragment.newInstance(),
                    R.id.container_for_fragments,
                    FoundFragment.TAG
                )
            }
        }
    }
}