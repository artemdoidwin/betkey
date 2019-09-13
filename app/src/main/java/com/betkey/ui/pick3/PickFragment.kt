package com.betkey.ui.pick3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
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
        pick_price_sp.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, stakes)

        compositeDisposable.add(
            pick_bet_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
//                addFragment(
//                    LotteryWaitFragment.newInstance(),
//                    R.id.container_for_fragments,
//                    LotteryWaitFragment.TAG
//                )
            }
        )

        compositeDisposable.add(
            pick_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity!!.finish()
            }
        )

        number_picker1.minValue = 0
        number_picker1.maxValue = 9

    }

}