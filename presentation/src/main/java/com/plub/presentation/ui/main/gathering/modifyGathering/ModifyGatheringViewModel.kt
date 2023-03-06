package com.plub.presentation.ui.main.gathering.modifyGathering

import androidx.lifecycle.viewModelScope
import com.plub.domain.model.vo.home.recruitdetailvo.RecruitDetailResponseVo
import com.plub.domain.usecase.GetRecruitDetailUseCase
import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.ui.main.gathering.modifyGathering.recruit.ModifyRecruitPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyGatheringViewModel @Inject constructor(
    private val getRecruitDetailUseCase: GetRecruitDetailUseCase
) : BaseViewModel<ModifyGatheringPageState>(ModifyGatheringPageState()) {

    fun getGatheringInfoDetail(plubbingId: Int) {
        viewModelScope.launch {
            getRecruitDetailUseCase(plubbingId).collect { state ->
                inspectUiState(
                    state,
                    succeedCallback = { handleGetGatheringInfoSuccess(plubbingId, it) },
                    individualErrorCallback = null
                )
            }
        }
    }

    fun handleUiState(uiState: ModifyGatheringPageState) {
        if(uiState != ModifyGatheringPageState()) emitEventFlow(ModifyGatheringEvent.InitViewPager)
    }

    private fun handleGetGatheringInfoSuccess(plubbingId: Int, data: RecruitDetailResponseVo) {
        updateUiState { uiState ->
            uiState.copy(
                modifyRecruitPageState = getRecruitPageState(plubbingId, data)
            )
        }
    }

    private fun getRecruitPageState(plubbingId: Int, data: RecruitDetailResponseVo): ModifyRecruitPageState {
        return ModifyRecruitPageState(
            plubbingId = plubbingId,
            title = data.recruitTitle,
            name = data.plubbingName,
            goal = data.plubbingGoal,
            introduce = data.recruitIntroduce,
            plubbingMainImgUrl = data.plubbingMainImage,
        )
    }

}