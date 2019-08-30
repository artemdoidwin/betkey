package com.betkey.ui.scanTickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_outcome_blank.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BlankOutcomeFragment : BaseFragment(){

    private val viewModel by sharedViewModel<MainViewModel>()

    companion object {
        const val TAG = "BlankOutcomeFragment"
        private const val KEY_OUTCOME = "outcome"

        fun newInstance(outcome: String?): BlankOutcomeFragment{
            val args = Bundle()
            args.putString(KEY_OUTCOME, outcome)
            val instance = BlankOutcomeFragment()
            instance.arguments = args
            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_outcome_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(KEY_OUTCOME)?.also {
            outcome_name.text = it
        }

        back_btn.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.link.value = null
    }
}