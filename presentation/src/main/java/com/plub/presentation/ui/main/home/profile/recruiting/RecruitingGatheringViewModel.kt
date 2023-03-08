package com.plub.presentation.ui.main.home.profile.recruiting

import com.plub.domain.model.enums.MyPageDetailViewType
import com.plub.domain.model.vo.home.myPage.MyPageDetailTitleVo
import com.plub.domain.model.vo.home.myPage.MyPageDetailVo
import com.plub.domain.model.vo.myPage.MyPageApplicationsVo
import com.plub.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecruitingGatheringViewModel @Inject constructor() : BaseViewModel<RecruitingGatheringState>(RecruitingGatheringState()) {

    fun getPageDetail(){
        val list = arrayListOf(MyPageDetailVo(
            viewType = MyPageDetailViewType.TOP,
            title = MyPageDetailTitleVo(
                title = "요란한 한줄",
                date = arrayListOf("월", "화", "수", "목", "금", "토"),
                time = "17:30",
                position = "경기도 의정부시 동일로 474번길"
            )
        ),
            MyPageDetailVo(
                viewType = MyPageDetailViewType.OTHER_APPLICANTS,
            ),
            MyPageDetailVo(
                viewType = MyPageDetailViewType.APPLICANTS,
                application = MyPageApplicationsVo(
                    profileImage = "https://plub.s3.ap-northeast-2.amazonaws.com/plubbing/mainImage/sports1.png",
                    name = "조경석",
                    date = "2001. 11. 06"
                )
            ),
            MyPageDetailVo(
                viewType = MyPageDetailViewType.APPLICANTS,
                application = MyPageApplicationsVo(
                    profileImage = "https://plub.s3.ap-northeast-2.amazonaws.com/plubbing/mainImage/sports1.png",
                    name = "조경석",
                    date = "2001. 11. 06"
                )
            ),
            MyPageDetailVo(
                viewType = MyPageDetailViewType.APPLICANTS,
                application = MyPageApplicationsVo(
                    profileImage = "https://plub.s3.ap-northeast-2.amazonaws.com/plubbing/mainImage/sports1.png",
                    name = "조경석",
                    date = "2001. 11. 06"
                )
            ),

        )
        updateUiState {uiState->
            uiState.copy(
                detailList = list
            )
        }
    }
}