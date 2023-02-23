package com.plub.data.dto.recruitDetail.host

import com.google.gson.annotations.SerializedName
import com.plub.data.base.DataDto

data class EndRecruitResponse(
    @SerializedName("plubbingId")
    val plubbingId : Int = -1
) : DataDto