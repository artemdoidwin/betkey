package com.betkey.ui.sportbetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.Market
import com.betkey.network.models.Team
import com.betkey.ui.MainViewModel
import com.betkey.ui.sportbetting.marketsAdapters.MarketsAdapter
import com.betkey.utils.dateToString3
import com.betkey.utils.toFullDate
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_details.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class DetailsSportBitingFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailsSportBitingFragment"

        fun newInstance() = DetailsSportBitingFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: MarketsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sportbetting_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            detail_basket.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                addFragment(
                    BasketFragment.newInstance(),
                    R.id.container_for_fragments,
                    BasketFragment.TAG
                )
            }
        )
        compositeDisposable.add(
            details_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        viewModel.marketsRest.observe(myLifecycleOwner, Observer { event ->
            event?.also {
                initAdapter(it.markets.keys.toMutableList(), it.teams, it.markets)
                val commandName = "${it.teams["1"]?.name} - ${it.teams["2"]?.name}"
                details_head_text.text = commandName
                it.startTime?.toFullDate()?.also { dateString ->
                    details_date.text = dateString.dateToString3()
                }
            }
        })
    }

    private fun initAdapter(
        namesMarkets: MutableList<String>,
        teams: Map<String, Team>,
        markets: Map<String, Market>
    ) {
        val listPosition = mutableListOf(0, 1)
        adapter = MarketsAdapter(markets.toMutableMap(), teams) { bet ->
            adapter.setItems(namesMarkets, bet, listPosition)
        }
        recycler_markets.adapter = adapter
        adapter.setItems(namesMarkets, null, listPosition)
    }
}