package com.plub.presentation.ui.main.profile.active.myPost

import androidx.lifecycle.viewModelScope
import com.plub.domain.model.enums.DialogMenuItemType
import com.plub.domain.model.enums.DialogMenuType
import com.plub.domain.model.enums.PlubingBoardType
import com.plub.domain.model.vo.board.BoardRequestVo
import com.plub.domain.model.vo.board.PlubingBoardListVo
import com.plub.domain.model.vo.board.PlubingBoardVo
import com.plub.domain.model.vo.myPage.MyPageActiveRequestVo
import com.plub.domain.usecase.DeleteBoardUseCase
import com.plub.domain.usecase.GetMyPostUseCase
import com.plub.domain.usecase.PutBoardChangePinUseCase
import com.plub.presentation.base.BaseTestViewModel
import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.parcelableVo.ParsePlubingBoardVo
import com.plub.presentation.ui.main.plubing.board.PlubingBoardEvent
import com.plub.presentation.ui.main.plubing.board.PlubingBoardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class MyPageAllMyPostViewModel @Inject constructor(
    private val getMyPostUseCase: GetMyPostUseCase,
    private val putBoardChangePinUseCase : PutBoardChangePinUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase
) : BaseTestViewModel<MyPageAllMyPostState>() {

    companion object {
        private const val FIRST_CURSOR = 0
        private const val SCROLL_TOP = 0
    }

    private val boardListStateFlow: MutableStateFlow<List<PlubingBoardVo>> = MutableStateFlow(
        emptyList()
    )

    override val uiState: MyPageAllMyPostState = MyPageAllMyPostState(
        boardList = boardListStateFlow.asStateFlow()
    )

    private var plubingId by Delegates.notNull<Int>()
    private var isNetworkCall: Boolean = false
    private var isLastPage: Boolean = false
    private var cursorId: Int = FIRST_CURSOR
    private var scrollToPosition:Int? = null

    fun setPlubId(plubingId: Int) {
        this.plubingId = plubingId
    }

    fun onCompleteBoardCreate() {
        refresh()
    }

    fun onCompleteBoardEdit(vo: ParsePlubingBoardVo) {
        val domainVo = ParsePlubingBoardVo.mapToDomain(vo)
        updateEditFeedList(domainVo)
    }

    fun onClickMenuItemType(feedId: Int, item: DialogMenuItemType) {
        when (item) {
            DialogMenuItemType.BOARD_FIX_CLIP -> boardFixClip(feedId)
            DialogMenuItemType.BOARD_DELETE -> boardDelete(feedId)
            DialogMenuItemType.BOARD_REPORT -> emitEventFlow(MyPageAllMyPostEvent.GoToReportBoard(feedId))
            DialogMenuItemType.BOARD_EDIT -> emitEventFlow(MyPageAllMyPostEvent.GoToEditBoard(feedId))
            else -> Unit
        }
    }

    fun onClickBoard(feedId: Int) {
        emitEventFlow(MyPageAllMyPostEvent.GoToDetailBoard(feedId))
    }

    fun onBoardUpdated() {
        scrollToPosition?.let {
            emitEventFlow(MyPageAllMyPostEvent.ScrollToPosition(it))
            scrollToPosition = null
        }
    }

    fun onLongClickBoard(feedId: Int, isHost: Boolean, isAuthor: Boolean) {
        val menuType = when {
            isHost && isAuthor -> DialogMenuType.BOARD_LIST_HOST_AND_AUTHOR_TYPE
            isHost -> DialogMenuType.BOARD_LIST_HOST_TYPE
            isAuthor -> DialogMenuType.BOARD_AUTHOR_TYPE
            else -> DialogMenuType.BOARD_COMMON_TYPE
        }
        emitEventFlow(MyPageAllMyPostEvent.ShowMenuBottomSheetDialog(feedId, menuType))
    }


    fun onFetchBoardList() {
        isNetworkCall = true
        cursorUpdate()
        fetchPlubingBoardList()
    }

    private fun refresh() {
        isNetworkCall = true
        isLastPage = false
        cursorId = FIRST_CURSOR
        scrollToPosition = SCROLL_TOP
        fetchPlubingBoardList()
    }

    fun onScrollChanged(isBottom: Boolean, isDownScroll: Boolean) {
        if (isBottom && isDownScroll && !isLastPage && !isNetworkCall) onFetchBoardList()
    }

    private fun fetchPlubingBoardList() {
        val requestVo = MyPageActiveRequestVo(plubingId, cursorId)
        viewModelScope.launch {
            getMyPostUseCase(requestVo).collect {
                inspectUiState(it, ::onSuccessFetchPlubingBoardList)
            }
        }
    }

    private fun onSuccessFetchPlubingBoardList(vo: PlubingBoardListVo) {
        vo.run {
            val mergedList = getMergeList(content)
            updateBoardList(mergedList)
            isLastPage = last
            isNetworkCall = false
        }
    }

    private fun getMergeList(list: List<PlubingBoardVo>): List<PlubingBoardVo> {
        val originList = uiState.boardList.value
        val pinList = originList.filter { it.viewType == PlubingBoardType.CLIP_BOARD }
        return if (cursorId == FIRST_CURSOR) pinList + list else originList + list
    }

    private fun boardFixClip(feedId: Int) {
        viewModelScope.launch {
            val request = BoardRequestVo(plubingId, feedId)
            putBoardChangePinUseCase(request).collect {
                inspectUiState(it, {
                    updateDeletedFeedList(feedId)
                })
            }
        }
    }

    private fun boardDelete(feedId: Int) {
        viewModelScope.launch {
            val request = BoardRequestVo(plubingId, feedId)
            deleteBoardUseCase(request).collect {
                inspectUiState(it, {
                    updateDeletedFeedList(feedId)
                })
            }
        }
    }

    private fun updateDeletedFeedList(feedId: Int) {
        val deletedList = uiState.boardList.value.filterNot { it.feedId == feedId }
        updateBoardList(deletedList)
    }

    private fun updateEditFeedList(vo: PlubingBoardVo) {
        val newList = uiState.boardList.value.toMutableList()
        val idx = newList.indexOfFirst { it.feedId == vo.feedId }
        newList[idx] = vo
        updateBoardList(newList)
    }

    private fun updateBoardList(list:List<PlubingBoardVo>) {
        viewModelScope.launch {
            boardListStateFlow.update { list }
        }
    }

    private fun cursorUpdate() {
        cursorId = if (uiState.boardList.value.isEmpty()) FIRST_CURSOR
        else uiState.boardList.value.lastOrNull()?.feedId ?: FIRST_CURSOR
    }

    fun onClickBack(){
        emitEventFlow(MyPageAllMyPostEvent.GoToBack)
    }

}