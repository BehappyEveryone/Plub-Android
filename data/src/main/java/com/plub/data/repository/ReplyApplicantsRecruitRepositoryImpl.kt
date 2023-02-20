package com.plub.data.repository

import com.plub.data.api.RecruitApi
import com.plub.data.base.BaseRepository
import com.plub.data.mapper.applicantsRecruitMapper.replyMapper.ReplyApplicantsRecruitMapper
import com.plub.domain.UiState
import com.plub.domain.model.vo.home.applicantsRecruitVo.replyVo.ReplyApplicantsRecruitRequestVo
import com.plub.domain.model.vo.home.applicantsRecruitVo.replyVo.ReplyApplicantsRecruitResponseVo
import com.plub.domain.repository.ReplyApplicantsRecruitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReplyApplicantsRecruitRepositoryImpl @Inject constructor(
    private val recruitApi: RecruitApi
) : ReplyApplicantsRecruitRepository, BaseRepository(){
    override suspend fun postApprovalApplicants(requestVo: ReplyApplicantsRecruitRequestVo): Flow<UiState<ReplyApplicantsRecruitResponseVo>> {
        return apiLaunch(recruitApi.approvalApplicants(requestVo.plubbingId,requestVo.accountId), ReplyApplicantsRecruitMapper)
    }

    override suspend fun postRefuseApplicants(requestVo: ReplyApplicantsRecruitRequestVo): Flow<UiState<ReplyApplicantsRecruitResponseVo>> {
        return apiLaunch(recruitApi.refuseApplicants(requestVo.plubbingId,requestVo.accountId), ReplyApplicantsRecruitMapper)
    }

}