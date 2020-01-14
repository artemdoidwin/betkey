package com.betkey.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.utils.Translation
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.container_for_activity.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class LoginFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    //translation
    private var enterPassword = ""

    companion object {
        const val TAG = "LoginFragment"

        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.include_toolbar.visibility = View.GONE
        login_user_name.requestFocus()

        localeManager.loadTranslation()

        compositeDisposable.add(
            login_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
//                subscribe(viewModel.login("test_agent_1", "12345"), {
                subscribe(viewModel.login(login_user_name.text.toString().trim(), login_password.text.toString().trim()), {
                    showFragment(MainMenuFragment.newInstance(), R.id.container_for_fragments, MainMenuFragment.TAG)
                },{
                    if (it.message == null){
                        toast(enterPassword)
                    } else {
                        context?.also { con -> toast(setMessage(it, con)) }
                    }
                })
            }
        )
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        title.text = dictionary[Translation.Login.TITLE]
        login_user_name.hint = dictionary[Translation.Login.USERNAME_HINT]
        login_password.hint = dictionary[Translation.Login.PASSWORD_HINT]
        login_btn.text = dictionary[Translation.Login.BUTTON]
        enterPassword = dictionary[Translation.Login.ENTER_PASSWORD] ?: resources.getString(R.string.enter_password)
    }
}