package com.plub.data.dto.hobby

import com.google.gson.annotations.SerializedName
import com.plub.data.base.DataDto

data class CategoryImageResponse(
    @SerializedName("image")
    val image : String = ""
) : DataDto
