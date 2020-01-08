package com.betkey.ui.sportbetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_enter_bet_code.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EnterBetCodeFragment : BaseFragment() {
    companion object {
        const val TAG = "EnterBetCodeFragment"

        fun newInstance() = EnterBetCodeFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_bet_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lookup_btn.setOnClickListener {
            addFragment(
                BetDetailsFragment.newInstance(),
                R.id.container_for_fragments,
                BetDetailsFragment.TAG
            )
        }

        lookup_back_btn.setOnClickListener {
            popBackStack()
        }
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}