package com.betkey.ui.sportbetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.demo.fragment_sportbetting.*
import kotlinx.android.synthetic.main.fragment_sportbetting.*
import kotlinx.android.synthetic.main.fragment_sportbetting.sp_back_btn
import kotlinx.android.synthetic.main.fragment_sportbetting.sp_featured_btn
import kotlinx.android.synthetic.main.fragment_sportbetting.sp_lookup_booking__btn
import kotlinx.android.synthetic.main.fragment_sportbetting.sp_today_btn
import kotlinx.android.synthetic.main.fragment_sportbetting.sp_tomorrow_btn
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class SportbetingFragment : BaseFragment() {

    companion object {
        const val TAG = "SportbetingFragment"

        fun newInstance() = SportbetingFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sportbetting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sp_featured_btn.setOnClickListener {
            subscribe( viewModel.sportBetStartingSoon(), {
                addFragment(TodayEventsFragment.newInstance(), R.id.container_for_fragments, TodayEventsFragment.TAG)
            }, {
                toast(it.message.toString())
            })
        }

        sp_tomorrow_btn.setOnClickListener {
            subscribe( viewModel.sportBetTomorrow(), {
                addFragment(TodayEventsFragment.newInstance(), R.id.container_for_fragments, TodayEventsFragment.TAG)
            }, {
                toast(it.message.toString())
            })
        }

        sp_today_btn.setOnClickListener {
            subscribe( viewModel.sportBetToday(), {
                addFragment(TodayEventsFragment.newInstance(), R.id.container_for_fragments, TodayEventsFragment.TAG)
            }, {
                toast(it.message.toString())
            })
        }

        sp_lookup_booking__btn.setOnClickListener {
            addFragment(LookupFragment.newInstance(), R.id.container_for_fragments, LookupFragment.TAG)
        }
        sp_back_btn.setOnClickListener {
            activity?.also { it.finish() }
        }

        sp_enter_bet_code_btn.setOnClickListener {
            addFragment(EnterBetCodeFragment.newInstance(), R.id.container_for_fragments, EnterBetCodeFragment.TAG)
        }
    }
}