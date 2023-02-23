package com.plub.presentation.ui.main.home.categoryGathering.filter

import androidx.lifecycle.viewModelScope
import com.plub.domain.model.enums.DaysType
import com.plub.domain.model.vo.common.SelectedHobbyVo
import com.plub.domain.model.vo.common.SubHobbyVo
import com.plub.domain.usecase.GetSubHobbiesUseCase
import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.util.PlubLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GatheringFilterViewModel @Inject constructor(
    val getSubHobbiesUseCase: GetSubHobbiesUseCase
): BaseViewModel<GatheringFilterState>(GatheringFilterState()) {

    private var categoryName = ""
    private val selectedList: MutableList<SelectedHobbyVo> = mutableListOf()
    private val selectedDayList : MutableList<DaysType> = mutableListOf()

    fun fetchSubHobbies(categoryId : Int, categoryName : String){
        this.categoryName = categoryName
        viewModelScope.launch {
            getSubHobbiesUseCase(categoryId).collect{
                inspectUiState(it, ::handleSuccessFetchSubHobbies)
            }
        }
    }

    private fun handleSuccessFetchSubHobbies(vo : List<SubHobbyVo>){
        updateUiState { uiState ->
            uiState.copy(
                categoryName = categoryName,
                subHobbies = vo
            )
        }
    }

    fun onClickSubHobby(isClicked: Boolean, selectedHobbyVo: SelectedHobbyVo) {
        if (isClicked) removeHobby(selectedHobbyVo) else addHobby(selectedHobbyVo)
    }

    private fun addHobby(selectedHobbyVo: SelectedHobbyVo) {
        selectedList.add(selectedHobbyVo)
        updateSelectList()
        notifySubItem(selectedHobbyVo)
    }

    private fun removeHobby(selectedHobbyVo: SelectedHobbyVo) {
        selectedList.remove(selectedHobbyVo)
        updateSelectList()
        notifySubItem(selectedHobbyVo)
    }

    private fun updateSelectList() {
        updateUiState { ui ->
            ui.copy(
                hobbiesSelectedVo = ui.hobbiesSelectedVo.copy(selectedList)
            )
        }
    }

    private fun notifySubItem(selectedHobbyVo: SelectedHobbyVo) {
        emitEventFlow(GatheringFilterEvent.NotifySubHobby(selectedHobbyVo))
    }

    fun onClickAllDay(){
        if(selectedDayList.contains(DaysType.ALL)){
            selectedDayList.remove(DaysType.ALL)
        }
        else{
            selectedDayList.clear()
            selectedDayList.add(DaysType.ALL)
            selectedDayList.add(DaysType.MON)
            selectedDayList.add(DaysType.TUE)
            selectedDayList.add(DaysType.WED)
            selectedDayList.add(DaysType.THR)
            selectedDayList.add(DaysType.FRI)
            selectedDayList.add(DaysType.SAT)
            selectedDayList.add(DaysType.SUN)
        }
        updateSelectDayList()
        emitEventFlow(GatheringFilterEvent.ClickDay(selectedDayList))
    }

    fun onClickDay(daysType : DaysType){
        if(selectedDayList.contains(DaysType.ALL)){
            selectedDayList.remove(DaysType.ALL)
        }

        if(selectedDayList.contains(daysType)){
            selectedDayList.remove(daysType)
        }
        else{
            selectedDayList.add(daysType)
        }
        updateSelectDayList()
        emitEventFlow(GatheringFilterEvent.ClickDay(selectedDayList))
    }

    private fun updateSelectDayList(){
        updateUiState { uiState ->
            uiState.copy(
                dayList = selectedDayList
            )
        }
    }

    val updateSeekbarProgressAndPositionX: (progress: Int, position: Float) -> Unit =
        { progress, position ->
            updateSeekbarProgress(progress)
            updateSeekbarPositionX(position)
        }

    private fun updateSeekbarProgress(progress: Int) {
        updateUiState { uiState ->
            uiState.copy(
                seekBarProgress = progress,
                accountNum = progress + 4
            )
        }
    }

    private fun updateSeekbarPositionX(position: Float) {
        updateUiState { uiState ->
            uiState.copy(
                seekBarPositionX = position
            )
        }
    }

    fun onClickApply(){
        emitEventFlow(GatheringFilterEvent.GoToCategoryGathering(uiState.value))
    }

    fun onClickBack(){
        emitEventFlow(GatheringFilterEvent.GoToBack)
    }
}