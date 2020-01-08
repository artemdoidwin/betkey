package com.betkey.ui.sportbetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.betkey.R
import com.betkey.base.BaseFragment
import com.betkey.models.SportBetBasketModel
import com.betkey.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_bet_details.*
import kotlinx.android.synthetic.main.item_sportbetting_basket.*
import kotlinx.android.synthetic.main.item_sportbetting_basket.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BetDetailsFragment : BaseFragment() {
    companion object {
        const val TAG = "BetDetailsFragment"
        fun newInstance() = BetDetailsFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bet_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  model = SportBetBasketModel(
            idEvent = "A123",
            league = "France / Ligue",
            teamsName = "MARSEILLE / PSG",
            date = "Monday 12 June 14:00",
            marketKey = "Match Result Full Time",
            marketName = "",
            betWinName = "PSG",
            odds = "1.98",
            betKey = "",
            lineName = ""
        )

        bet_details.ligue.text = model.league
        bet_details.command_name.text = model.teamsName
        bet_details.date.text = model.date
        bet_details.match_result.text = model.marketKey
        bet_details.bet_command_name.text = model.betWinName
        bet_details.odds.text = model.odds

        bet_details.remove.visibility = View.GONE

        btn_add_bet.isEnabled = !viewModel.basketList.value?.contains(model)!!

        btn_go_to_betslip.setOnClickListener {
            showFragment(
                BasketFragment.newInstance(),
                R.id.container_for_fragments,
                BasketFragment.TAG
            )
        }

        btn_add_bet.setOnClickListener {
            viewModel.basketList.value?.also { list ->
                if(!list.contains(model)) {
                    list.add(model)
                }
            }
            btn_add_bet.isEnabled = !viewModel.basketList.value?.contains(model)!!
        }

        btn_back.setOnClickListener {
            popBackStack()
        }
    }

    override fun onTranslationReceived(dictionary: Map<String?, String?>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}