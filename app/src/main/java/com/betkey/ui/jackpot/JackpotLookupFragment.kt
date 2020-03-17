package com.betkey.ui.jackpot

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.utils.setMessage
import kotlinx.android.synthetic.main.fragment_jackpot_lookup.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class JackpotLookupFragment:BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "JackpotLookupFragment"

        fun newInstance() = JackpotLookupFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jackpot_lookup, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lookup_back_btn.setOnClickListener {
            popBackStack()
        }
        lookup_btn.setOnClickListener {
            subscribe(viewModel.betLookup(lookup_code_ET.text.toString()), {
                 showFragment(JackpotApproveFragment.newInstance(it), R.id.container_for_fragments, JackpotConfirmationFragment.TAG)
            }, {context?.also {con -> toast(setMessage(it, con))}})

        }


        lookup_code_ET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                lookup_btn.isEnabled = !s.isNullOrEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })



    }
}