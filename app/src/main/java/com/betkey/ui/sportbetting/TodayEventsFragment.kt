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
import com.betkey.ui.sportbetting.eventsAdapters.EventsAdapter
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_todays.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class TodayEventsFragment : BaseFragment() {

    companion object {
        const val TAG = "TodayEventsFragment"

        fun newInstance() = TodayEventsFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var productsListener: SportBettingListener
    private lateinit var currenpMap: Map<String, Map<String, List<Event>>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sportbetting_todays, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            bs_todays_basket.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                addFragment(
                    BasketFragment.newInstance(),
                    R.id.container_for_fragments,
                    BasketFragment.TAG
                )
            }
        )
        compositeDisposable.add(
            bs_todays_events_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )

        viewModel.sportBetToday.observe(myLifecycleOwner, Observer { sportBetToday ->
            sportBetToday?.also { map ->
                currenpMap = map
                head_text.text = resources.getString(R.string.sportbetting_todat_events)
                viewModel.basketList.value?.also { list ->
                    initAdapter(map, list)
                }
            }
        })
        viewModel.sportBetTomorrow.observe(myLifecycleOwner, Observer { sportBetTomorrow ->
            sportBetTomorrow?.also { map ->
                currenpMap = map
                head_text.text = resources.getString(R.string.sportbetting_tomorrow_events)
                viewModel.basketList.value?.also { list ->
                    initAdapter(map, list)
                }
            }
        })
        viewModel.sportBetStartingSoon.observe(myLifecycleOwner, Observer { sportBetStartingSoon ->
            sportBetStartingSoon?.also { map ->
                currenpMap = map
                head_text.text = resources.getString(R.string.sportbetting_starting_soon_events)
                viewModel.basketList.value?.also { list ->
                    initAdapter(map, list)
                }
            }
        })
        viewModel.basketList.observe(myLifecycleOwner, Observer {
            it?.also { basketList ->
                bs_todays_basket.text = it.size.toString()

                activity?.supportFragmentManager?.fragments?.also { fragmentList ->
                    fragmentList.filter { frag -> frag.isVisible }
                    val fragment = fragmentList[fragmentList.size - 1]
                    if (fragment !is TodayEventsFragment) {
                        initAdapter(currenpMap, basketList)
                    }
                }
            }
        })

        productsListener = object : SportBettingListener {
            override fun onCommandLeft(model: SportBetBasketModel) {
                changeBasketList(model)
            }

            override fun onIDraw(model: SportBetBasketModel) {
                changeBasketList(model)
            }

            override fun onCommandRight(model: SportBetBasketModel) {
                changeBasketList(model)
            }

            override fun onSetMarkets(eventId: String) {
                subscribe(viewModel.getSportbettingMarkets(eventId), {
                    addFragment(
                        DetailsSportBitingFragment.newInstance(),
                        R.id.container_for_fragments,
                        DetailsSportBitingFragment.TAG
                    )
                }, {
                    toast(it.message.toString())
                })
            }
        }
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

    private fun initAdapter(
        it: Map<String, Map<String, List<Event>>>, basketlist: MutableList<SportBetBasketModel>
    ) {
        it["football"]?.also { footballMap ->
            val adapter = EventsAdapter(
                basketlist,
                productsListener,
                footballMap.toMutableMap()
            )
            bs_todays_events_adapter.adapter = adapter
            val namesLeagues = footballMap.keys.toMutableList()
            adapter.setItems(namesLeagues)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.sportBetStartingSoon.value = null
        viewModel.sportBetTomorrow.value = null
        viewModel.sportBetToday.value = null
        viewModel.basketList.value = mutableListOf()
    }


}