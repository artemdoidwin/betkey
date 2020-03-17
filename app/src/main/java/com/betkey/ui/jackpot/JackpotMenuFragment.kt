package com.betkey.ui.jackpot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.sportbetting.TodayEventsFragment
import kotlinx.android.synthetic.main.fragment_jackpot_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class JackpotMenuFragment: BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "JackpotMenuFragment"

        fun newInstance() = JackpotMenuFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jackpot_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        jackpotPlaceButton.setOnClickListener {
            addFragment(JackpotFragment.newInstance(), R.id.container_for_fragments, JackpotFragment.TAG)
        }
        jackpotLookupButton.setOnClickListener {
            addFragment(JackpotLookupFragment.newInstance(), R.id.container_for_fragments, JackpotLookupFragment.TAG)
        }
        jackpotBackButton.setOnClickListener {
            activity?.also { it.finish() }
        }
    }

}