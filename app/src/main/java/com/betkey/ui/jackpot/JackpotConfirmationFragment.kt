package com.betkey.ui.jackpot

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
import com.betkey.ui.UsbPrinterActivity
import com.betkey.utils.*
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jacpot_confirmation.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class JackpotConfirmationFragment : BaseFragment() {

    companion object {
        const val TAG = "JackpotConfirmationFragment"

        fun newInstance() = JackpotConfirmationFragment()
    }

    private lateinit var gamesAdapter: BetsAdapter
    private val viewModel by sharedViewModel<MainViewModel>()
    private val buf = StringBuilder()
    val space = "\n---------------------------\n"

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
                confirmation_ticket_created.text = createDateString(it.created!!)
            }
        })

        viewModel.lookupBets.observe(myLifecycleOwner, Observer { obj ->
            obj?.also {
                createString()
                for (i in it.events!!.indices) {
                    buf.append(space)
                    createBetList(it.events!![i])
                }
//                UsbPrinterActivity.start(
//                    activity!!,
//                    buf.toString(),
//                    "",
//                    viewModel.agentBet.value!!.message_data?.betCode!!
//                )
            }
        })

        viewModel.jackpotInfo.observe(myLifecycleOwner, Observer { jackpotInfo ->
            jackpotInfo?.also {
                it.coupon?.also { coupon ->
                    confirmation_coupon_id.text = coupon.coupon?.id.toString()

                    viewModel.wallets.value?.also {
                        val price = "${coupon.defaultStake} ${viewModel.wallets.value!![0].currency} "
                        confirmation_ticket_price.text = price
                    }

                    val date = coupon.coupon?.expires?.toFullDate()!!.dateToString()
                    confirmation_last_entry.text = date
                }
            }
        })
    }

    private fun createString() {
        buf.append(
            "---------------------------\n" +
                    "${resources.getString(R.string.jackpot_confirmation_ticket_number).toUpperCase()} ${viewModel.agentBet.value!!.message_data?.couponId.toString()}\n" +
                    "${resources.getString(R.string.jackpot_game_code).toUpperCase()} ${viewModel.agentBet.value!!.message_data?.betCode!!}\n" +
                    "${resources.getString(R.string.jackpot_game_date_time).toUpperCase()} ${createDateString(viewModel.agentBet.value!!.created!!)}\n" +
                    "${resources.getString(R.string.scan_detail_type).toUpperCase()} ${viewModel.ticket.value!!.platformUnit!!.name}" +
                    space +
                    resources.getString(R.string.jackpot_confirmation_bet_details).toUpperCase()
        )
    }

    private fun createBetList(event: Event) {
        val date = event.time!!.date!!.toFullDate2().dateToString2()
        val friendlyId = event.friendlyId!!
        val league = event.league!!.name
        val team1Name = (event.teams["1"])!!.name
        val team2Name = (event.teams["2"])!!.name
        val marketName = event.market_name!!
        val bet = event.bet!!
        buf.append(
            "$date\n" +
                    "$friendlyId $league\n" +
                    "$team1Name -\n" +
                    "$team2Name\n" +
                    "$marketName $bet"
        )
    }
}