package com.betkey.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.UsbPrinterActivity
import kotlinx.android.synthetic.main.fragment_view_report.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ViewReportFragment(private val dateTimeFrom:String,private val dateTimeTo:String):BaseFragment() {
    companion object {
        const val TAG = "ViewReportFragment"

        fun newInstance(dateTimeFrom:String,dateTimeTo:String) = ViewReportFragment(dateTimeFrom,dateTimeTo)
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportPeriod.text = "$dateTimeFrom - $dateTimeTo"
        viewModel.report.observe(this, Observer {report->
            reportName.text = "${report.baseData.firstName} ${report.baseData.lastName}"
            reportCurrency.text = report.wallet.currency
            reportBalance.setValue(report.wallet.balance)
            reportTotalTickets.setValue(report.soldSportBetsCount)
            reportTotalAmount.setValue(report.soldSportBetsAmount)
            reportPayoutTickets.setValue(report.payoutSportBetsCount)
            reportPayoutAmount.setValue(report.payoutSportBetsAmount)
            reportCalnceledTickets.setValue(report.cancelledSportBetsCount)
            reportCalnceledAmount.setValue(report.cancelledSportBetsAmount)
            reportJackpotTickets.setValue(report.soldJackpotBetsCount)
            reportJackpotAmount.setValue(report.soldJackpotBetsAmount)
            reportDeposits.setValue(report.depositsCount)
            reportDepositAmount.setValue(report.depositsTotalAmount)
            reportWithdrawals.setValue(report.withdrawalsCount)
            reportWithdrawalAmount.setValue(report.withdrawalsTotalAmount)

        })

        reportPrintButton.setOnClickListener {
            UsbPrinterActivity.startReport(activity!!, dateTimeFrom,dateTimeTo)
        }
        report_back_btn.setOnClickListener {
            popBackStack()
        }
    }
}