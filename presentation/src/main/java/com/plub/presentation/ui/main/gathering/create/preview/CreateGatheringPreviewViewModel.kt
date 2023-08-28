package com.plub.presentation.ui.main.gathering.create.preview

import com.plub.domain.model.vo.signUp.hobbies.SignUpHobbiesVo
import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.util.PlubUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateGatheringPreviewViewModel @Inject constructor()
    : BaseViewModel<CreateGatheringPreviewPageState>(CreateGatheringPreviewPageState()) {

    fun updateMyInfoUrl(signHobbiesUpVo : SignUpHobbiesVo) {
        updateUiState { ui ->
            ui.copy(profileUrl = PlubUser.info.profileImage)
        }
    }
}