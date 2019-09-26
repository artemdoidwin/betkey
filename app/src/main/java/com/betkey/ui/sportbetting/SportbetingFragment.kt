package com.betkey.ui.sportbetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting.*
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

        compositeDisposable.add(
            sp_featured_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe( viewModel.sportBetStartingSoon(), {
                    addFragment(TodayEventsFragment.newInstance(), R.id.container_for_fragments, TodayEventsFragment.TAG)
                }, {
                    toast(it.message.toString())
                })
            }
        )
        compositeDisposable.add(
            sp_tomorrow_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe( viewModel.sportBetTomorrow(), {
                    addFragment(TodayEventsFragment.newInstance(), R.id.container_for_fragments, TodayEventsFragment.TAG)
                }, {
                    toast(it.message.toString())
                })
            }
        )
        compositeDisposable.add(
            sp_today_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe( viewModel.sportBetToday(), {
                    addFragment(TodayEventsFragment.newInstance(), R.id.container_for_fragments, TodayEventsFragment.TAG)
                }, {
                    toast(it.message.toString())
                })
            }
        )
        compositeDisposable.add(
            sp_lookup_booking__btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe( viewModel.sportBetToday(), {
                    addFragment(LookupFragment.newInstance(), R.id.container_for_fragments, LookupFragment.TAG)
                }, {
                    toast(it.message.toString())
                })
            }
        )

        compositeDisposable.add(
            sp_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity?.also { it.finish() }
            }
        )
    }
}