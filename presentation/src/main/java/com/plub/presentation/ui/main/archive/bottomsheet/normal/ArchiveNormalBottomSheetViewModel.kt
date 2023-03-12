package com.plub.presentation.ui.main.archive.bottomsheet.normal

import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.ui.PageState
import com.plub.presentation.ui.main.archive.bottomsheet.ArchiveDotsBottomSheetEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArchiveNormalBottomSheetViewModel @Inject constructor(
) : BaseViewModel<PageState.Default>(PageState.Default) {

    fun goToReport(){
        emitEventFlow(ArchiveDotsBottomSheetEvent.GoToReport)
    }
}