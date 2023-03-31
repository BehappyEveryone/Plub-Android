package com.plub.domain.repository

import com.plub.domain.UiState
import com.plub.domain.model.enums.MyPageGatheringStateType
import com.plub.domain.model.vo.myPage.MyPageGatheringVo
import com.plub.domain.model.vo.myPage.MyPageMyApplicationVo
import kotlinx.coroutines.flow.Flow

interface MyPageRepository {
    suspend fun getMyGathering(request: MyPageGatheringStateType): Flow<UiState<MyPageGatheringVo>>
    suspend fun getMyApplicationWithPlubInfo(request: Int) : Flow<UiState<MyPageMyApplicationVo>>
}