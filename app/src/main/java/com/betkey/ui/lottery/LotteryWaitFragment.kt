package com.betkey.ui.lottery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.LotteryOrPickModel
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_lotery_wait.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class LotteryWaitFragment : BaseFragment() {

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
                putIntegerArrayList(LIST_NUMBERS, list as ArrayList<Int>)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lotery_wait, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val num = if (list?.size == 6) {
            "${list?.get(0)} - ${list?.get(1)} - ${list?.get(2)} - ${list?.get(3)} - ${list?.get(4)} - ${list?.get(
                5
            )}"
        } else {
            "${list?.get(0)} - ${list?.get(1)} - ${list?.get(2)}"
        }

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

        val winsCombinations = mutableListOf<String>()

        viewModel.player.observe(this, Observer { link ->
            when (activity!!.localClassName) {
                "ui.lottery.LotteryActivity" -> {
                    winsCombinations.add("X 1")
                    winsCombinations.add("X 10")
                    winsCombinations.add("X 100")
                    winsCombinations.add("X 1000")
                    winsCombinations.add("X 10 000")
                    winsCombinations.add("X 100 000")

                    viewModel.lotteryOrPick.value =
                        LotteryOrPickModel(
                            stake!!,
                            "2142",
                            "Monday 12.oct.19:45",
                            num.replace(" ", ""),
                            winsCombinations
                        )
                    UsbPrinterActivity.start(activity!!, UsbPrinterActivity.LOTTERY)
                }
                "ui.pick3.PickActivity" -> {
                    winsCombinations.add("X 1")
                    winsCombinations.add("X 10")
                    winsCombinations.add("X 300")

                    viewModel.lotteryOrPick.value = LotteryOrPickModel(
                        stake!!,
                        "9999",
                        "Monday 19:45",
                        num.replace(" ", ""),
                        winsCombinations
                    )
                    UsbPrinterActivity.start(activity!!, UsbPrinterActivity.PICK_3)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.lotteryOrPickRequest.value = "l"
    }
}