package com.betkey.ui.sportbetting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.ui.MainViewModel
import com.betkey.utils.Translation
import com.betkey.utils.setMessage
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_sportbetting_basket.*
import kotlinx.android.synthetic.main.fragment_sportbetting_lookup_booking.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class LookupFragment : BaseFragment() {

    companion object {
        const val TAG = "LookupFragment"

        fun newInstance() = LookupFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sportbetting_lookup_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            lookup_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                subscribe(viewModel.publicBetslips(lookup_code_ET.text.toString()), {
                    addFragment(
                        BasketFragment.newInstance(),
                        R.id.container_for_fragments,
                        BasketFragment.TAG
                    )
//                 }, { (it as? HttpException)?.also { ex -> toast(ex.message()) } })
                 }, {context?.also {con -> toast(setMessage(it, con))} })
            }
        )

        compositeDisposable.add(
            lookup_back_btn.clicks().throttleLatest(1, TimeUnit.SECONDS).subscribe {
                popBackStack()
            }
        )
        lookup_code_ET.addTextChangedListener(textWatcher)
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        sp_title.text = dictionary[Translation.LookUp.TITLE]
        sp_title2.text = dictionary[Translation.LookUp.ENTER_BOOKING_CODE]
        lookup_btn.text = dictionary[Translation.LOOKUP]
        lookup_back_btn.text = dictionary[Translation.BACK]
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(searchText: CharSequence, start: Int, before: Int, count: Int) {
            lookup_btn.isEnabled = searchText.isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }
}