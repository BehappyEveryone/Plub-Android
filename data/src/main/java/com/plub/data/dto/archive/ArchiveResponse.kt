package com.plub.data.dto.archive

import com.google.gson.annotations.SerializedName
import com.plub.data.base.DataDto

data class ArchiveResponse(
    @SerializedName("totalPages")
    val totalPages : Int = -1,
    @SerializedName("totalElements")
    val totalElements : Int = -1,
    @SerializedName("last")
    val last : Int = -1,
    @SerializedName("content")
    val content : List<ArchiveContentResponse> = emptyList()

):DataDto