package com.betkey.ui.sportbetting

import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import org.jetbrains.anko.doAsync
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class DetailsSportBitingFragment : BaseFragment() {

    companion object {
        const val TAG = "DetailsSportBitingFragment"
        var time: Long = 0
        fun newInstance() = DetailsSportBitingFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: MarketsAdapter
    private lateinit var currentEvent: Event
    private var openListPosition =  mutableListOf(0, 1)

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
                Handler().post { initAdapter() }

                val commandName = "${it.teams["1"]?.name} - ${it.teams["2"]?.name}"
                details_head_text.text = commandName
                it.startTime?.toFullDate()?.also { dateString ->
                    details_date.text = dateString.dateToString3()
                }
            }
        })
        viewModel.basketList.observe(myLifecycleOwner, Observer {
            it?.also { detail_basket.text = it.size.toString() }
            Log.d("TIMER", "${System.currentTimeMillis() - time} basketList")
            Handler().post { initAdapter() }
        })
    }

    private fun initAdapter() {
        adapter = MarketsAdapter(
            openListPosition,
            viewModel.basketList.value!!,
            currentEvent.markets.toMutableMap(),
            currentEvent.teams,
            currentEvent.id!!,
            currentEvent.league?.name!!,
            currentEvent.startTime!!.toFullDate().dateToString3()
        ) { basketModel, newListPos ->
            time = System.currentTimeMillis()
            changeBasketList(basketModel)
            openListPosition = newListPos
        }
        recycler_markets.adapter = adapter
        time = System.currentTimeMillis()
    }

    private fun changeBasketList(model: SportBetBasketModel) {
        viewModel.basketList.value?.also { list ->
            val newList = mutableListOf<SportBetBasketModel>()
            newList.addAll(list)
            list.forEach { modelInList ->
                if (modelInList.idEvent == model.idEvent) {
                    newList.remove(modelInList)
                }
            }
            newList.add(model)
            viewModel.basketList.value = newList
        }
    }
}