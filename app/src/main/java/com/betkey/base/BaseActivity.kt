package com.betkey.base

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.betkey.R
import com.betkey.utils.LoadingUiHelper

abstract class BaseActivity : AppCompatActivity(){
    private var currentFragment: BaseFragment? = null
    private var progressDialog: LoadingUiHelper.ProgressDialogFragment? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentFragment?.activityResult(requestCode, resultCode, data)

        Log.d(LOG_TAG, "BaseActivity $requestCode $resultCode $currentFragment")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        currentFragment?.requestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(LOG_TAG, "BaseActivity onRequestPermissionsResult $currentFragment")
    }

    fun showLoading(){
        if (progressDialog == null){
            progressDialog = LoadingUiHelper.showProgress()
        }
        progressDialog?.show(supportFragmentManager, LoadingUiHelper.ProgressDialogFragment.TAG)
    }

    fun hideLoading(){
        progressDialog?.dismiss()
        progressDialog = null
    }


    fun addFragment(fragment: BaseFragment, idContainer: Int, tag: String){
        currentFragment = fragment
        supportFragmentManager
            .beginTransaction()
            .add(idContainer, fragment)
            .addToBackStack(tag)
            .commit()
    }

    protected fun replaceFragment(fragment: BaseFragment) {
        currentFragment = fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    protected fun replaceFragmentInIdContent(fragment: BaseFragment) {
        currentFragment = fragment
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    protected fun replaceFragmentWithoutBackstack(fragment: BaseFragment) {
        currentFragment = fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    protected fun showFragment(fragment: BaseFragment, idContainer: Int, tag: String){
        currentFragment = fragment
        supportFragmentManager
            .beginTransaction()
            .replace(idContainer, fragment)
            .addToBackStack(tag)
            .commit()
    }

    protected fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(window?.currentFocus?.windowToken, 0)
    }

    companion object {
        const val LOG_TAG = "myLogs"
    }
}