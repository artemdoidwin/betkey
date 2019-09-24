package com.betkey.ui.sportbetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.Event
import com.betkey.ui.MainViewModel
import com.betkey.ui.sportbetting.eventsAdapters.EventsAdapter
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_todays.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class TodaysEventsFragment : BaseFragment() {

    companion object {
        const val TAG = "TodaysEventsFragment"

        fun newInstance() = TodaysEventsFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var productsListener: SportBettingListener

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
            sportBetToday?.also {
                head_text.text = resources.getString(R.string.sportbetting_todat_events)
                initAdapter(it)
            }
        })
        viewModel.sportBetTomorrow.observe(myLifecycleOwner, Observer { sportBetTomorrow ->
            sportBetTomorrow?.also {
                head_text.text = resources.getString(R.string.sportbetting_tomorrow_events)
                initAdapter(it)
            }
        })
        viewModel.sportBetStartingSoon.observe(myLifecycleOwner, Observer { sportBetStartingSoon ->
            sportBetStartingSoon?.also {
                head_text.text = resources.getString(R.string.sportbetting_starting_soon_events)
                initAdapter(it)
            }
        })

        productsListener = object : SportBettingListener {
            override fun onCommandLeft(commandName: String) {
                Log.d("", "")
            }

            override fun onIDraw(commandName: String) {
                Log.d("", "")
            }

            override fun onCommandRight(commandName: String) {
                Log.d("", "")
            }
            override fun onSetMarkets(eventId: String) {
                subscribe( viewModel.getSportbettingMarkets(eventId), {
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

    private fun initAdapter(it: Map<String, Map<String, List<Event>>>) {
        it["football"]?.also { footballMap ->
            val adapter = EventsAdapter(
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
    }


}