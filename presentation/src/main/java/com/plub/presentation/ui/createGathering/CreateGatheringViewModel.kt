package com.plub.presentation.ui.createGathering

import com.plub.domain.model.state.PageState
import com.plub.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateGatheringViewModel @Inject constructor() : BaseViewModel<PageState.Default>(PageState.Default) {

}