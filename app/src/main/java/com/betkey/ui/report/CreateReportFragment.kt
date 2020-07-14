package com.betkey.ui.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.jackpot.JackpotFragment
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.fragment_sportbetting_basket.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CreateReportFragment: BaseFragment() {

    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val formatForView = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    companion object {
        const val TAG = "CreateReportFragment"

        fun newInstance() = CreateReportFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customReportBtn.setOnClickListener {

            try {
                val from = datePickerFrom.getDate()
                val to = datePickerTo.getDate()
                subscribe(viewModel.getReport(format.format(from),format.format(to)),{
                    showFragment(ViewReportFragment.newInstance(formatForView.format(datePickerFrom.getDate()),formatForView.format(datePickerTo.getDate())), R.id.container_for_fragments, ViewReportFragment.TAG)
                },{
                    it.printStackTrace()
                })
            } catch (e: Exception) {
                Toast.makeText(context, "Please, enter date correctly", Toast.LENGTH_LONG).show()
            }
        }
        reportTodayBtn.setOnClickListener {
            Log.d(TAG,"report today clicked")
            subscribe(viewModel.getReport(format.format(Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY,0)
                set(Calendar.MINUTE,0)}.time),format.format(Date())),{
                showFragment(ViewReportFragment.newInstance(formatForView.format(Date()),formatForView.format(Date())), R.id.container_for_fragments, ViewReportFragment.TAG)
            },{
                it.printStackTrace()
            })
        }

        reportYesterdayBtn.setOnClickListener {
            val yesterdayStart = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR,-1)
                set(Calendar.HOUR_OF_DAY,0)
                set(Calendar.MINUTE,0)
            }
            val yesterdayEnd = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR,-1)
                set(Calendar.HOUR_OF_DAY,23)
                set(Calendar.MINUTE,59)
        }

            subscribe(viewModel.getReport(format.format(yesterdayStart.time),format.format(yesterdayEnd.time)),{
                showFragment(ViewReportFragment.newInstance(formatForView.format(yesterdayStart.time),formatForView.format(yesterdayEnd.time)), R.id.container_for_fragments, ViewReportFragment.TAG)
            },{
                it.printStackTrace()
            })
        }


    }
}