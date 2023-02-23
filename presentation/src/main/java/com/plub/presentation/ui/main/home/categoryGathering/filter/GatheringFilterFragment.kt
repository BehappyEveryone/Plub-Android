package com.plub.presentation.ui.main.home.categoryGathering.filter

import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.plub.domain.model.enums.DaysType
import com.plub.domain.model.vo.common.SelectedHobbyVo
import com.plub.presentation.R
import com.plub.presentation.base.BaseFragment
import com.plub.presentation.databinding.FragmentCategoryGatheringFilterBinding
import com.plub.presentation.ui.common.decoration.GridSpaceDecoration
import com.plub.presentation.ui.sign.hobbies.adapter.HobbiesAdapter
import com.plub.presentation.ui.sign.hobbies.adapter.SubHobbiesAdapter
import com.plub.presentation.util.PlubLogger
import com.plub.presentation.util.px
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GatheringFilterFragment :
    BaseFragment<FragmentCategoryGatheringFilterBinding, GatheringFilterState, GatheringFilterViewModel>(
        FragmentCategoryGatheringFilterBinding::inflate
    ) {

    companion object{
        const val ALL = 0
        const val MON = 1
        const val TUE = 2
        const val WED = 3
        const val THR = 4
        const val FRI = 5
        const val SAT = 6
        const val SUN = 7


        const val ITEM_SPAN_SIZE = 4
        const val HORIZONTAL_SPACE = 12
        const val VERTICAL_SPACE = 8
    }

    private val listAdapter: SubHobbiesAdapter by lazy {
        SubHobbiesAdapter(object : HobbiesAdapter.Delegate {
            override val selectedList: List<SelectedHobbyVo>
                get() = viewModel.uiState.value.hobbiesSelectedVo.hobbies

            override fun onClickExpand(hobbyId: Int) {

            }

            override fun onClickSubHobby(isClicked: Boolean, selectedHobbyVo: SelectedHobbyVo) {
                viewModel.onClickSubHobby(isClicked, selectedHobbyVo)
            }

            override fun onClickLatePick() {

            }
        })
    }

    private val dayButtonList : MutableList<Button> = mutableListOf()
    private val gatheringFilterFragmentArgs : GatheringFilterFragmentArgs by navArgs()
    override val viewModel: GatheringFilterViewModel by viewModels()

    override fun initView() {
        binding.apply {
            vm = viewModel
            setButtonList()
            initRecycler()
        }
        viewModel.fetchSubHobbies(gatheringFilterFragmentArgs.categoryId, gatheringFilterFragmentArgs.categoryName)
    }

    private fun setButtonList(){
        binding.apply {
            dayButtonList.add(buttonAllDay)
            dayButtonList.add(buttonMonday)
            dayButtonList.add(buttonTuesday)
            dayButtonList.add(buttonWednesday)
            dayButtonList.add(buttonThursday)
            dayButtonList.add(buttonFriday)
            dayButtonList.add(buttonSaturday)
            dayButtonList.add(buttonSunday)
        }
    }

    private fun initRecycler(){
        binding.apply {
            recyclerViewSubHobbies.apply {
                layoutManager = GridLayoutManager(context, ITEM_SPAN_SIZE)
                addItemDecoration(GridSpaceDecoration(ITEM_SPAN_SIZE, HORIZONTAL_SPACE.px, VERTICAL_SPACE.px, false))
                adapter = listAdapter
            }
        }
    }

    override fun initStates() {
        super.initStates()
        repeatOnStarted(viewLifecycleOwner) {
            launch {
                viewModel.uiState.collect {
                    listAdapter.submitList(it.subHobbies)
                    updateDayButton(it.dayList)
                }
            }

            launch {
                viewModel.eventFlow.collect{
                    inspectEventFlow(it as GatheringFilterEvent)
                }
            }
        }
    }

    private fun inspectEventFlow(event: GatheringFilterEvent) {
        when(event) {
            is GatheringFilterEvent.NotifySubHobby -> {
                listAdapter.updateOnClick(event.vo.subId)
            }
            is GatheringFilterEvent.GoToCategoryGathering -> {
                goToCategoryGathering(event.pageState)
            }
            is GatheringFilterEvent.GoToBack -> {
                findNavController().popBackStack()
            }
            is GatheringFilterEvent.ClickDay -> {
                updateDayButton(event.dayList)
            }
        }
    }

    private fun updateDayButton(dayList : List<DaysType>){
        uncheckedDayButton()
        dayList.forEach {
            when(it){
                DaysType.MON -> { checkedDayButton(MON) }
                DaysType.TUE -> { checkedDayButton(TUE) }
                DaysType.WED -> { checkedDayButton(WED) }
                DaysType.THR -> { checkedDayButton(THR) }
                DaysType.FRI -> { checkedDayButton(FRI) }
                DaysType.SAT -> { checkedDayButton(SAT) }
                DaysType.SUN -> { checkedDayButton(SUN) }
                DaysType.ALL -> { checkedDayButton(ALL) }
            }
        }
    }

    private fun checkedDayButton(day : Int){
        dayButtonList[day].setBackgroundResource(R.drawable.bg_rectangle_filled_5f5ff9_radius_8)
        dayButtonList[day].setTextColor(resources.getColor(R.color.white))
    }

    private fun uncheckedDayButton(){
        dayButtonList.forEach {
            it.setBackgroundResource(R.drawable.bg_rectangle_empty_8c8c8c_radius_8)
            it.setTextColor(resources.getColor(R.color.color_8c8c8c))
        }
    }

    private fun goToCategoryGathering(pageState: GatheringFilterState){
        val action = GatheringFilterFragmentDirections.actionFilterToCategoryGathering(
            categoryId = gatheringFilterFragmentArgs.categoryId,
            categoryName = gatheringFilterFragmentArgs.categoryName,
            filter = pageState
        )
        findNavController().navigate(action)
    }
}