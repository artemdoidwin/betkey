package com.betkey.ui.jackpot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_jacpot_confirmation.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class JackpotConfirmationFragment : BaseFragment() {

    companion object {
        const val TAG = "JackpotConfirmationFragment"

        fun newInstance() = JackpotConfirmationFragment()
    }

    private lateinit var gamesAdapter: BetsAdapter
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.betkey.R.layout.fragment_jacpot_confirmation, container, false)
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
//                confirmation_ticket_number.text = it.message_data?.couponId.toString()
//                confirmation_ticket_code.text = it.message_data?.betCode

                val cal = Calendar.getInstance()
                cal.timeInMillis = it.created!!
                val d = cal.get(Calendar.HOUR_OF_DAY)
                val e = cal.get(Calendar.MINUTE)
                val r = cal.get(Calendar.SECOND)
                val dt = cal.get(Calendar.DATE)

                confirmation_ticket_created.text = ""
            }
        })
    }

    fun Date.toSimpleString(): String {
        if (this.time == 0L) return ""

//        val tz = LocalStorage().getTimeZone()

        return SimpleDateFormat("MM/dd/yyyy", Locale.US)
//            .apply { timeZone = TimeZone.getTimeZone(tz) }
            .format(this)
    }
}