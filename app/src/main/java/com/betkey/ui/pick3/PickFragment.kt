package com.betkey.ui.pick3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.lottery.LotteryWaitFragment
import com.betkey.utils.isLowBattery
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_lottery.*
import kotlinx.android.synthetic.main.fragment_pick.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class PickFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "PickFragment"

        fun newInstance() = PickFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pick, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stakes = arrayOf(10, 50, 100, 150)
        pick_price_sp.adapter =
            ArrayAdapter(context!!, android.R.layout.simple_spinner_item, stakes)

        compositeDisposable.add(
            pick_bet_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                if (!isLowBattery(context!!)){
                    addFragment(
                        LotteryWaitFragment.newInstance(
                            pick_price_sp.selectedItem.toString(),
                            addNumbers()
                        ),
                        R.id.container_for_fragments,
                        LotteryWaitFragment.TAG
                    )
                }
            }
        )
        compositeDisposable.add(
            pick_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity!!.finish()
            }
        )

        number_picker1.minValue = 0
        number_picker1.maxValue = 9
        number_picker2.minValue = 0
        number_picker2.maxValue = 9
        number_picker3.minValue = 0
        number_picker3.maxValue = 9
        number_picker1.setOnValueChangedListener { _, _, _ -> pick_bet_btn.isEnabled = true }
        number_picker2.setOnValueChangedListener { _, _, _ -> pick_bet_btn.isEnabled = true }
        number_picker3.setOnValueChangedListener { _, _, _ -> pick_bet_btn.isEnabled = true }

        viewModel.lotteryOrPickRequest.observe(this, Observer { model ->
            model?.also { m ->
                pick_price_sp.setSelection(0)
                number_picker1.value = 0
                number_picker2.value = 0
                number_picker3.value = 0
                pick_bet_btn.isEnabled = false
            }
        })
    }

    private fun addNumbers(): MutableList<Int> {
        val list = mutableListOf<Int>()
        list.add(number_picker1.value)
        list.add(number_picker2.value)
        list.add(number_picker3.value)
        return list
    }
}