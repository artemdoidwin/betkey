package com.betkey.ui.sportbetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.SportBetBasketModel
import com.betkey.network.models.Event
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
    private lateinit var currentEvent: Event

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
                currentEvent = it
                viewModel.basketList.value?.also { list ->
                    initAdapter(it, list)
                }

                val commandName = "${it.teams["1"]?.name} - ${it.teams["2"]?.name}"
                details_head_text.text = commandName
                it.startTime?.toFullDate()?.also { dateString ->
                    details_date.text = dateString.dateToString3()
                }
            }
        })
        viewModel.basketList.observe(myLifecycleOwner, Observer {
            it?.also { detail_basket.text = it.size.toString() }

            initAdapter(currentEvent, it)
        })
    }

    private fun initAdapter(
        event: Event,
        basketList: MutableList<SportBetBasketModel>
    ) {
        val openListPosition = mutableListOf(0, 1)
        adapter = MarketsAdapter(
            basketList,
            event.markets.toMutableMap(),
            event.teams,
            event.id!!,
            event.league?.name!!,
            event.startTime!!.toFullDate().dateToString3()
        ) { basketModel ->
            changeBasketList(basketModel)
            adapter.setItems(event.markets.keys.toMutableList(), openListPosition)
        }
        recycler_markets.adapter = adapter
        adapter.setItems(event.markets.keys.toMutableList(), openListPosition)
    }

    private fun changeBasketList(model: SportBetBasketModel) {
        viewModel.basketList.value?.also { list ->
            val newList = mutableListOf<SportBetBasketModel>()
            newList.addAll(list)
            list.forEach { modelInList ->
                if (modelInList.bet == model.bet) {
                    newList.remove(modelInList)
                }
            }
            newList.add(model)
            viewModel.basketList.value = newList
        }
    }
}