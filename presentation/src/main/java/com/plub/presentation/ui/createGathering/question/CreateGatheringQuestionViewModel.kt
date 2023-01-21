package com.plub.presentation.ui.createGathering.question

import androidx.lifecycle.viewModelScope
import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.ui.createGathering.peopleNumber.CreateGatheringPeopleNumberPageState
import com.plub.presentation.util.PlubLogger
import com.plub.presentation.util.deepCopy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGatheringQuestionViewModel @Inject constructor() :
    BaseViewModel<CreateGatheringQuestionPageState>(CreateGatheringQuestionPageState()) {

    private val maxQuestionCount = 5

    /**
     * Pair first = size, second = position
     */
    private val _showBottomSheetDeleteQuestion =
        MutableSharedFlow<Pair<Int, Int>>(0, 1, BufferOverflow.DROP_OLDEST)
    /**
     * Pair first = size, second = position
     */
    val showBottomSheetDeleteQuestion: SharedFlow<Pair<Int, Int>> =
        _showBottomSheetDeleteQuestion.asSharedFlow()

    private val _performClickNoQuestionRadioButton =
        MutableSharedFlow<Unit>(0, 1, BufferOverflow.DROP_OLDEST)
    val performClickNoQuestionRadioButton: SharedFlow<Unit> =
        _performClickNoQuestionRadioButton.asSharedFlow()

    fun initUiState(savedUiState: CreateGatheringQuestionPageState) {
        updateUiState { uiState ->
            uiState.copy(
                _questions = savedUiState.copy(isNeedQuestionCheck = true).questions.deepCopy(),
                isNeedQuestionCheck = savedUiState.isNeedQuestionCheck,
                needUpdateRecyclerView = true,
                isAddQuestionButtonVisible = savedUiState.isAddQuestionButtonVisible
            )
        }
    }

    fun onClickNeedQuestionButton() {
        viewModelScope.launch {
            updateUiState { uiState ->
                uiState.copy(
                    isNeedQuestionCheck = true,
                    needUpdateRecyclerView = true
                )
            }
        }
    }

    fun onClickNoQuestionButton() {
        viewModelScope.launch {
            updateUiState { uiState ->
                uiState.copy(
                    isNeedQuestionCheck = false,
                    needUpdateRecyclerView = false
                )
            }
        }
    }

    fun onClickRecyclerViewDeleteButton(position: Int) {
        viewModelScope.launch {
            _showBottomSheetDeleteQuestion.emit(Pair(uiState.value.questions.size, position))
        }
    }

    fun updateQuestion(data: CreateGatheringQuestion, text: String) {
        updateUiState { uiState ->
            uiState.questions.find { it.key == data.key }?.question = text
            PlubLogger.logD("테스트", "업뎃 전 : ${uiState.questions}")

            val newUiState = uiState.copy(
                needUpdateRecyclerView = false,
                isAddQuestionButtonVisible = uiState.questions.isNotEmpty() && uiState.questions.find { it.question.isBlank() } == null && uiState.questions.size != maxQuestionCount
            )

            PlubLogger.logD("테스트", "업뎃 후 : ${uiState.questions}")
            newUiState
        }
    }

    fun addQuestion() {
        updateUiState { uiState ->
            val key = uiState.questions.lastOrNull()?.key ?: 0
            val emptyQuestion = CreateGatheringQuestion(
                key = key + 1,
                position = uiState.questions.size + 1
            )
            uiState.copy(
                _questions = uiState.questions.plus(emptyQuestion),
                needUpdateRecyclerView = true,
                isAddQuestionButtonVisible = false
            )
        }
    }

    fun onClickBottomSheetDelete(size: Int, position: Int) {
        deleteQuestionOrMakeQuestionSizeToOne(position)
        viewModelScope.launch {
            if(size == 1)
                _performClickNoQuestionRadioButton.emit(Unit)
        }
    }

    private fun deleteQuestionOrMakeQuestionSizeToOne(position: Int) {
        val data = uiState.value.questions.find { it.position == position } ?: return

        updateUiState { uiState ->
            val deleteIndex = uiState.questions.indexOf(data)
            val temp = uiState.questions.minus(data).deepCopy().ifEmpty { listOf(CreateGatheringQuestion()) }

            updateQuestionPosition(deleteIndex, temp)
            uiState.copy(
                _questions = temp,
                needUpdateRecyclerView = true,
                isAddQuestionButtonVisible = temp.find { it.question.isBlank() } == null
            )
        }
    }

    private fun updateQuestionPosition(changeIndex: Int, questions: List<CreateGatheringQuestion>) {
        var key = uiState.value.questions.lastOrNull()?.key ?: 0
        questions.forEachIndexed { index, createGatheringQuestion ->
            if (index >= changeIndex) {
                createGatheringQuestion.position = index + 1
                createGatheringQuestion.key = ++key
                createGatheringQuestion.question = questions[index].question
            }
        }
    }
}