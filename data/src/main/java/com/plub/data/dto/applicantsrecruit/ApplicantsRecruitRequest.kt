package com.plub.data.dto.applicantsrecruit

import com.google.gson.annotations.SerializedName
import com.plub.data.base.DataDto

data class ApplicantsRecruitRequest(
    @SerializedName("answers")
    val answers : List<ApplicantsRecruitRequestAnswerList> = emptyList()
) : DataDto