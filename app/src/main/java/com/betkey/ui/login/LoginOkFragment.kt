package com.betkey.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.deposit.DepositActivity
import com.betkey.ui.jackpot.JackpotActivity
import com.betkey.ui.lottery.LotteryActivity
import com.betkey.ui.pick3.PickActivity
import com.betkey.ui.report.ReportActivity
import com.betkey.ui.scanTickets.ScanTicketsActivity
import com.betkey.ui.sportbetting.SportBettingActivity
import com.betkey.ui.withdrawal.WithdrawalActivity
import com.betkey.utils.LANGUAGE_EN
import com.betkey.utils.LANGUAGE_FR
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_login_ok.*
import kotlinx.android.synthetic.main.view_spiner_flag.view.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.fragment_login_ok.deposits_btn
import kotlinx.android.synthetic.main.fragment_login_ok.include_toolbar
import kotlinx.android.synthetic.main.fragment_login_ok.jackpot_btn
import kotlinx.android.synthetic.main.fragment_login_ok.language_spinner
import kotlinx.android.synthetic.main.fragment_login_ok.logout_btn
import kotlinx.android.synthetic.main.fragment_login_ok.lottery_btn
import kotlinx.android.synthetic.main.fragment_login_ok.pick_btn
import kotlinx.android.synthetic.main.fragment_login_ok.scan_btn
import kotlinx.android.synthetic.main.fragment_login_ok.sport_betting_btn
import kotlinx.android.synthetic.main.fragment_login_ok.withdrawal_btn


class LoginOkFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var restartActivity = false
    companion object {
        const val TAG = "LoginOkFragment"
        const val REQUEST_CODE = 12345
        const val RESTART_ACTIVITY = "RESTART_ACTIVITY"
        fun newInstance() = LoginOkFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_ok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()

        activity?.include_toolbar?.visibility = View.GONE
        viewModel.wallets.observe(myLifecycleOwner, Observer { wallets ->
            wallets?.also {
                val nameToDisplay = when {
                    viewModel.agent.value?.firstName?.isNotEmpty() == true -> "${viewModel.agent.value?.firstName} - "
                    viewModel.agent.value?.username?.isNotEmpty() == true -> "${viewModel.agent.value?.username} - "
                    else -> ""
                }
                val text = "$nameToDisplay${String.format("%.2f", it[0].balance)} ${it[0].currency.toUpperCase()}"
                text_toolbar.text = text
            }
        })
    }

    override fun onResume() {
        super.onResume()
        subscribe(viewModel.getAgentWallets(), {})
    }

    private fun initButtons() {
        compositeDisposable.add(
            scan_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                if (hasPermissions(context!!, *permissions)) {
                    ScanTicketsActivity.start(activity!!)
                } else {
                    requestPermissions((permissions), REQUEST_CODE)
                }
            }
        )
        compositeDisposable.add(
            sport_betting_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                SportBettingActivity.start(activity!!)
            }
        )
        compositeDisposable.add(
            jackpot_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                JackpotActivity.start(activity!!)
            }
        )
        compositeDisposable.add(
            deposits_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                DepositActivity.start(activity!!)
            }
        )
        compositeDisposable.add(
            withdrawal_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                WithdrawalActivity.start(activity!!)
            }
        )
        compositeDisposable.add(
            lottery_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                LotteryActivity.start(activity!!)
            }
        )
        compositeDisposable.add(
            pick_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                PickActivity.start(activity!!)
            }
        )
        compositeDisposable.add(
            logout_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe(viewModel.logout(), {
                    clearStack()
                    showFragment(LoginFragment.newInstance(), R.id.container_for_fragments, LoginFragment.TAG)
                }, {
                    context?.also {con -> toast(setMessage(it, con))}
                })
            }
        )

        compositeDisposable.add(
            reportBtn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                ReportActivity.start(activity!!)
            }
        )
        val language = resources.getStringArray(R.array.languages)
        val adapter = CountryAdapter(context!!, R.layout.view_spiner_flag, language)
        language_spinner.adapter = adapter
        language_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (language[position]) {
                    LANGUAGE_EN -> context?.also { switchLocale(it, LANGUAGE_EN) }
                    LANGUAGE_FR -> context?.also { switchLocale(it, LANGUAGE_FR) }
                }
            }
        }
        when (viewModel.getLocale()) {
            LANGUAGE_EN -> language_spinner.setSelection(0)
            LANGUAGE_FR -> language_spinner.setSelection(1)
        }
    }

    private fun switchLocale(context: Context, language: String) {
        viewModel.setNewLocale(context, language)
        if (restartActivity) {

            val i = Intent(context, LoginActivity::class.java)
            i.putExtra(RESTART_ACTIVITY, true)

            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        restartActivity = true
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, resultCodes: IntArray) {
        if (requestCode == REQUEST_CODE && resultCodes.count { it == PackageManager.PERMISSION_GRANTED } == resultCodes.size) {
            ScanTicketsActivity.start(activity!!)
        } else {
            toast(R.string.no_permission)
        }
    }

    class CountryAdapter(context: Context, textViewResourceId: Int, objects: Array<String?>) :
        ArrayAdapter<String?>(context, textViewResourceId, objects) {

        private val languages: Array<String?> = objects
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return getCustomView(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return getCustomView(position, convertView, parent)
        }

        private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val row: View = context.layoutInflater.inflate(R.layout.view_spiner_flag, parent, false)
            when(languages[position]){
                LANGUAGE_EN -> row.icon.setImageResource(R.drawable.eng)
                LANGUAGE_FR -> row.icon.setImageResource(R.drawable.france)
            }
            return row
        }
    }
}