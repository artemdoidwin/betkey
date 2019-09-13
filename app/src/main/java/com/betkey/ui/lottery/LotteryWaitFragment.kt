package com.betkey.ui.lottery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_lotery_wait.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class LotteryWaitFragment  : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    private val stake by lazy { arguments?.getString(STAKE) }
    private val list by lazy { arguments?.getIntegerArrayList(LIST_NUMBERS) }

    companion object {
        const val TAG = "LotteryWaitFragment"
        private const val STAKE = "stake_lottery"
        private const val LIST_NUMBERS = "list_numbers"

        fun newInstance(stake: String, list: List<Int>) = LotteryWaitFragment().apply {
            arguments = Bundle().apply {
                putString(STAKE, stake)
                putIntegerArrayList(LIST_NUMBERS, list as ArrayList<Int> )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lotery_wait, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val num = "${list?.get(0)} - ${list?.get(1)} - ${list?.get(2)} - ${list?.get(3)} - ${list?.get(4)} - ${list?.get(5)}"
        lottery_wait_numbers.text = num
        lottery_wait_price.text = stake

        compositeDisposable.add(
            lottery_wait_play_again_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        compositeDisposable.add(
            lottery_wait_home_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity!!.finish()
            }
        )
        viewModel.link.observe(this, Observer { link ->
            //            link?.also { l ->
//                subscribe(viewModel.checkTicket(l), {
//
//                }, { toast(it.message.toString()) })
//            }
        })
    }
}