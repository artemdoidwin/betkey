package com.betkey.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.login.LoginOkFragment
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.container_for_activity.*
import kotlinx.android.synthetic.main.fragment_find_player.*
import kotlinx.android.synthetic.main.fragment_login.*
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


        compositeDisposable.add(
            deposit_find_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
//                if (deposit_find_amount_ET.text.length >= 9) {
                    subscribe(
//                        viewModel.findPlayer(deposit_find_amount_ET.text.toString()), {
                        viewModel.findPlayer("35621001240"), {
                            if (it.errors.isNotEmpty() && it.errors[0].code == 33){
                                addFragment(NoPlayerFoundFragment.newInstance(), R.id.container_for_fragments, NoPlayerFoundFragment.TAG)
                            }else{
                                addFragment(FoundFragment.newInstance(), R.id.container_for_fragments, FoundFragment.TAG)
                            }
                        }, {
                            toast(it.message.toString())
                        }
                    )
//                }
            }
        )

        compositeDisposable.add(
            deposit_find_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity?.also { it.finish() }
            }
        )
    }
}