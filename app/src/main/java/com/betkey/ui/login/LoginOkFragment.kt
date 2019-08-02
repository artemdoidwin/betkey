package com.betkey.ui.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.ui.scanTickets.ScanTicketsActivity
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_login_ok.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class LoginOkFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "LoginOkFragment"
        const val REQUEST_CODE = 12345

        fun newInstance() = LoginOkFragment().apply {
            arguments = Bundle().apply {
                //                    putInt("pos", id)
//                    putString("name", name)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_ok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.text_toolbar.text = "rrr"

        initButtons()
    }

    private fun initButtons() {
        compositeDisposable.add(
            logout_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe(viewModel.logout(), {
                    clearStack()
                    showFragment(LoginFragment.newInstance(), R.id.login_container, LoginFragment.TAG)
                }, {
                    toast(it.message.toString())
                })
            }
        )
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
}