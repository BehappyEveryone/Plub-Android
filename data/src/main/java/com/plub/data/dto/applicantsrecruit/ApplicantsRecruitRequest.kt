package com.plub.data.dto.applicantsrecruit

import com.plub.data.base.DataDto

data class ApplicantsRecruitRequest(
    val answers : List<ApplicantsRecruitRequestAnswerList> = emptyList()
) : DataDto