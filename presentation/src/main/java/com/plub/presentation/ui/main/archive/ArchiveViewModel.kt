package com.plub.presentation.ui.main.archive

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImageView
import com.plub.domain.model.enums.ArchiveAccessType
import com.plub.domain.model.enums.DialogMenuItemType
import com.plub.domain.model.enums.UploadFileType
import com.plub.domain.model.vo.archive.*
import com.plub.domain.model.vo.media.UploadFileRequestVo
import com.plub.domain.model.vo.media.UploadFileResponseVo
import com.plub.domain.usecase.DeleteArchiveUseCase
import com.plub.domain.usecase.GetAllArchiveUseCase
import com.plub.domain.usecase.GetDetailArchiveUseCase
import com.plub.domain.usecase.PostUploadFileUseCase
import com.plub.presentation.base.BaseTestViewModel
import com.plub.presentation.util.ImageUtil
import com.plub.presentation.util.PlubingInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    val postUploadFileUseCase: PostUploadFileUseCase,
    val getAllArchiveUseCase: GetAllArchiveUseCase,
    val getDetailArchiveUseCase: GetDetailArchiveUseCase,
    val deleteArchiveUseCase: DeleteArchiveUseCase,
    val imageUtil: ImageUtil
)  :BaseTestViewModel<ArchivePageState>() {

    companion object{
        const val FIRST_CURSOR = 0
    }
    private var title : String = ""
    private var cursorId : Int = FIRST_CURSOR
    private var isNetworkCall : Boolean = false
    private var plubbingId : Int = -1
    private var isLastPage : Boolean = false
    private var cameraTempImageUri: Uri? = null

    private val titleStateFlow : MutableStateFlow<String> = MutableStateFlow("")
    private val archiveListStateFlow : MutableStateFlow<List<ArchiveContentResponseVo>> = MutableStateFlow(emptyList())

    override val uiState: ArchivePageState = ArchivePageState(
        titleStateFlow.asStateFlow(),
        archiveListStateFlow.asStateFlow()
    )

    fun refresh(){
        title = PlubingInfo.info.name
        plubbingId = PlubingInfo.info.plubingId
        cursorId = 0
        viewModelScope.launch {
            titleStateFlow.update { title }
            archiveListStateFlow.update { emptyList() }
        }
    }

    fun fetchArchivePage(){
        val request = BrowseAllArchiveRequestVo(plubbingId, cursorId)
        viewModelScope.launch {
            getAllArchiveUseCase(request).collect{ state ->
                inspectUiState(state, ::handleSuccessFetchArchives)
            }
        }
    }

    private fun handleSuccessFetchArchives(vo : ArchiveCardResponseVo){
        isNetworkCall = false
        isLastPage = vo.last

        viewModelScope.launch {
            titleStateFlow.update { title }
            archiveListStateFlow.update { getMergeList(vo.content) }
        }
    }

    private fun getMergeList(list : List<ArchiveContentResponseVo>) : List<ArchiveContentResponseVo>{
        val originList = uiState.archiveList.value
        return if(originList.isEmpty() || cursorId == FIRST_CURSOR) list else originList + list
    }

    fun onScrollChanged(isBottom: Boolean, isDownScroll: Boolean) {
        if (isBottom && isDownScroll && !isLastPage && !isNetworkCall) onFetchArchiveList()
    }

    fun onFetchArchiveList() {
        isNetworkCall = true
        cursorUpdate()
        fetchArchivePage()
    }

    private fun cursorUpdate() {
        cursorId = if (uiState.archiveList.value.isEmpty()) FIRST_CURSOR
        else uiState.archiveList.value.lastOrNull()?.archiveId ?: FIRST_CURSOR
    }

    fun seeDetailDialog(id : Int){
        val request = DetailArchiveRequestVo(plubbingId, id)
        viewModelScope.launch {
            getDetailArchiveUseCase(request).collect{ state ->
                inspectUiState(state, ::handleSuccessFetchDetailArchive)
            }
        }
    }

    private fun handleSuccessFetchDetailArchive(vo : ArchiveDetailResponseVo){
        emitEventFlow(ArchiveEvent.SeeDetailArchiveDialog(vo))
    }

    fun onClickBack(){
        emitEventFlow(ArchiveEvent.GoToBack)
    }

    fun onClickUploadBottomSheet(){
        emitEventFlow(ArchiveEvent.ClickUploadBottomSheet)
    }

    private fun uploadImageFile(file : File?){
        val request = file?.let { UploadFileRequestVo(UploadFileType.ARCHIVE, it) }
        viewModelScope.launch {
            if (request != null) {
                postUploadFileUseCase(request).collect{ state ->
                    inspectUiState(state, ::handleSuccessUploadImage)
                }
            }
        }
    }

    private fun handleSuccessUploadImage(vo : UploadFileResponseVo){
        emitEventFlow(ArchiveEvent.GoToArchiveUpload(vo.fileUrl, title))
    }

    fun seeBottomSheet(type : ArchiveAccessType, id : Int){
        when(type){
            ArchiveAccessType.HOST -> emitEventFlow(ArchiveEvent.SeeDotsHostBottomSheet(id))
            ArchiveAccessType.NORMAL -> emitEventFlow(ArchiveEvent.SeeDotsNormalBottomSheet(id))
            ArchiveAccessType.AUTHOR -> emitEventFlow(ArchiveEvent.SeeDotsAuthorBottomSheet(id))
        }
    }


    private fun updateArchiveList(list : List<ArchiveContentResponseVo>){
        viewModelScope.launch {
            archiveListStateFlow.update { list }
        }
    }

    fun goToEdit(archiveId : Int){
        emitEventFlow(ArchiveEvent.GoToEdit(title, archiveId))
    }

    fun goToReport(archiveId: Int){
        emitEventFlow(ArchiveEvent.GoToReport(archiveId))
    }

    fun onClickDotsMenuItemType(type : DialogMenuItemType, archiveId: Int){
        when(type){
            DialogMenuItemType.ARCHIVE_DELETE -> { deleteArchive(archiveId) }
            DialogMenuItemType.ARCHIVE_REPORT -> { goToReport(archiveId) }
            DialogMenuItemType.ARCHIVE_EDIT -> { goToEdit(archiveId) }
            else -> {}
        }
    }

    private fun deleteArchive(archiveId: Int) {
        val request = DetailArchiveRequestVo(plubbingId, archiveId)
        viewModelScope.launch {
            deleteArchiveUseCase(request).collect { state ->
                inspectUiState(state, { handleSuccessDelete(archiveId) })
            }
        }
    }

    private fun handleSuccessDelete(archiveId: Int) {
        val originList = uiState.archiveList.value
        val mutableOriginList = originList.toMutableList()
        originList.forEach {
            if(it.archiveId == archiveId) mutableOriginList.remove(it)
        }
        updateArchiveList(mutableOriginList)
    }

    fun onClickImageMenuItemType(type: DialogMenuItemType) {
        when(type) {
            DialogMenuItemType.CAMERA_IMAGE -> {
                cameraTempImageUri = imageUtil.getUriFromTempFileInExternalDir().also {
                    emitEventFlow(ArchiveEvent.GoToCamera(it))
                }
            }
            DialogMenuItemType.ALBUM_IMAGE -> emitEventFlow(ArchiveEvent.GoToAlbum)
            else -> {}
        }
    }

    fun proceedGatheringImageFromCameraResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            cameraTempImageUri?.let { uri ->
                emitCropImageAndOptimizeEvent(uri)
            }
        }
    }

    private fun emitCropImageAndOptimizeEvent(uri: Uri) {
        emitEventFlow(
            ArchiveEvent.CropImageAndOptimize(
            imageUtil.getCropImageOptions(uri)
        )
        )
    }

    fun proceedGatheringImageFromGalleryResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                emitCropImageAndOptimizeEvent(uri)
            }
        }
    }

    fun proceedCropImageResult(result: CropImageView.CropResult) {
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                val file = imageUtil.uriToOptimizeImageFile(uri)
                uploadImageFile(file)
            }
        }
    }
}