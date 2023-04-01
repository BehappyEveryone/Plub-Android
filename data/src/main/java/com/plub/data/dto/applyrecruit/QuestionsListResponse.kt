package com.plub.data.dto.applyrecruit

import com.google.gson.annotations.SerializedName
import com.plub.data.base.DataDto

data class QuestionsListResponse(
    @SerializedName("questions")
    val questions : List<QuestionDataResponse> = emptyList()
) : DataDto