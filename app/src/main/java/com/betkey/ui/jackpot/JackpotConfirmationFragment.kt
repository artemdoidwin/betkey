package com.betkey.ui.jackpot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.network.models.BetLookupObj
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import com.betkey.utils.*
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jacpot_confirmation.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class JackpotConfirmationFragment(private val betLookup: BetLookupObj? = null) : BaseFragment() {

    companion object {
        const val TAG = "JackpotConfirmationFragment"

        fun newInstance(betLookup: BetLookupObj? = null) = JackpotConfirmationFragment(betLookup)
    }

    private lateinit var gamesAdapter: BetsAdapter
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jacpot_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            confirmation_home_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                Log.d("", "")
            }
        )

        if (betLookup!=null){
            gamesAdapter = betLookup.events?.mapIndexed { i,v->
                Pair("${context?.resources?.getString(R.string.jackpot_game)} $i ${v.teams["1"]!!.name} - ${v.teams["2"]!!.name}",v.bet) }?.let {
                BetsAdapter(
                    it
                )
            }!!
            bet_adapter.adapter = gamesAdapter
        }
        viewModel.betsDetailsList.value?.also {
            gamesAdapter = BetsAdapter(it)
            bet_adapter.adapter = gamesAdapter
        }

        compositeDisposable.add(
            confirmation_home_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                activity?.also { it.finish() }
            }
        )

        viewModel.agentBet.observe(myLifecycleOwner, Observer { bet ->
            bet?.also {
                confirmation_ticket_number.text = it.message_data?.couponId.toString()
                confirmation_ticket_code.text = it.message_data?.betCode
                viewModel.wallets.value?.also {wallets ->
                    val price = "${it.message_data?.stake} ${wallets[0].currency} "
                    confirmation_ticket_price.text = price
                }
                confirmation_ticket_created.text = dateString(it.created!!)
                UsbPrinterActivity.start(activity!!, UsbPrinterActivity.JACKPOT)
            }
        })

        viewModel.jackpotInfo.observe(myLifecycleOwner, Observer { jackpotInfo ->
            jackpotInfo?.also {
                it.coupon?.also { coupon ->
                    confirmation_coupon_id.text = coupon.coupon?.id.toString()
                    val date = coupon.coupon?.expires?.toFullDate()!!.dateToString()
                    confirmation_last_entry.text = date
                }
            }
        })
    }

    override fun onDestroyView() {
        viewModel.betsDetailsList.value = null
        super.onDestroyView()
    }
}