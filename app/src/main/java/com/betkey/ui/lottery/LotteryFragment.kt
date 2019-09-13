package com.betkey.ui.lottery

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.betkey.base.BaseFragment
import com.betkey.models.LotteryModel
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_lottery.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit


class LotteryFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: NumberAdapter
    private lateinit var list: MutableList<LotteryModel>

    companion object {
        const val TAG = "LotteryFragment"

        fun newInstance() = LotteryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_lottery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stakes = arrayOf(10, 50, 100, 150)
        lottery_price_sp.adapter = ArrayAdapter(context!!, R.layout.simple_spinner_item, stakes)

        initAdapter()

        compositeDisposable.add(
            lottery_bet_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                addFragment(
                    LotteryWaitFragment.newInstance(
                        lottery_price_sp.selectedItem.toString(),
                        list.filter { model -> model.isSelected }.map { it.number }
                    ),
                    com.betkey.R.id.container_for_fragments,
                    LotteryWaitFragment.TAG
                )
            }
        )
        compositeDisposable.add(
            lottery_autopick_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                initList()
                autoPeak()
            }
        )
        compositeDisposable.add(
            lottery_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity!!.finish()
            }
        )
    }

    private fun autoPeak() {
        doAsync {
            var filteredList = listOf<LotteryModel>()
            while (filteredList.size != 6) {
                val index = (0..48).random()
                list.removeAt(index)
                list.add(index, LotteryModel(number = index + 1, isSelected = true))
                filteredList = list.filter { model -> model.isSelected }
            }
            runOnUiThread {
                adapter.setItems(list)
                lottery_bet_btn.isEnabled = true
            }
        }
    }

    private fun initAdapter() {
        initList()
        adapter = NumberAdapter {
            val filteredList = list.filter { model -> model.isSelected }
            if (filteredList.size != 6 || filteredList.contains(it)) {
                list.removeAt(it.number - 1)
                if (it.isSelected) {
                    list.add(it.number - 1, it.copy(isSelected = false))
                } else {
                    list.add(it.number - 1, it.copy(isSelected = true))
                }
                adapter.setItems(list)
                lottery_bet_btn.isEnabled = list.filter { model -> model.isSelected }.size == 6
            }
        }
        adapter.setItems(list)
        lottery_games_adapter.adapter = adapter
    }

    private fun initList() {
        list = mutableListOf()
        for (i in 1..49) {
            list.add(LotteryModel(false, i))
        }
    }
}