package com.plub.domain.model.enums

enum class MyPageGatheringStateType(val type:String) {
    ACTIVE("ACTIVE"), END("END"), WAIT("WAIT"), RECRUITING("RECRUITING");

    companion object {
        fun valuesOf(type: String): MyPageGatheringStateType {
            return MyPageGatheringStateType.values().find {
                it.type == type
            } ?: MyPageGatheringStateType.END
        }
    }
}