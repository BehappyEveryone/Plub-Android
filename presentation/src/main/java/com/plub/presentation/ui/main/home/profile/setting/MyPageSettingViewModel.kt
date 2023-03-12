package com.plub.presentation.ui.main.home.profile.setting

import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImageView
import com.plub.domain.error.NicknameError
import com.plub.domain.model.enums.DialogMenuItemType
import com.plub.domain.model.enums.UploadFileType
import com.plub.domain.model.vo.media.UploadFileRequestVo
import com.plub.domain.model.vo.media.UploadFileResponseVo
import com.plub.domain.model.vo.signUp.profile.NicknameCheckRequestVo
import com.plub.domain.usecase.GetNicknameCheckUseCase
import com.plub.domain.usecase.PostUploadFileUseCase
import com.plub.presentation.R
import com.plub.presentation.base.BaseViewModel
import com.plub.presentation.util.ImageUtil
import com.plub.presentation.util.PermissionManager
import com.plub.presentation.util.PlubUser
import com.plub.presentation.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyPageSettingViewModel @Inject constructor(
    val resourceProvider: ResourceProvider,
    val imageUtil: ImageUtil,
    val getNicknameCheckUseCase: GetNicknameCheckUseCase,
    val postUploadFileUseCase: PostUploadFileUseCase
) : BaseViewModel<MyPageSettingState>(MyPageSettingState()) {

    private var isNetworkCall:Boolean = false
    private var cameraTempImageUri: Uri? = null

    fun onInitProfile() {
        updateUiState { ui ->
            ui.copy(
                profileImage = PlubUser.info.profileImage,
                nickname = PlubUser.info.nickname,
                introduce = PlubUser.info.introduce,
                introduceCount = getIntroduceCountSpannableString(PlubUser.info.introduce.length.toString())
            )
        }
        onTextChangedAfter()
    }

    fun onTextChangedAfter() {
        val nickname = uiState.value.nickname
        if(nickname == PlubUser.info.nickname){
            handleNicknameCheckSuccess(true)
        }else{
            fetchNicknameCheck(nickname)
        }
    }

    fun fetchNicknameCheck(nickname: String) {
        isNetworkCall = true
        viewModelScope.launch {
            val request = NicknameCheckRequestVo(nickname, this)
            getNicknameCheckUseCase(request).collect { state ->
                inspectUiState(state, ::handleNicknameCheckSuccess) { _, individual ->
                    handleNicknameCheckError(individual as NicknameError)
                }
            }
        }
    }

    fun onClickProfileImage() {
        checkPermission {
            emitEventFlow(MyPageSettingEvent.ShowSelectImageBottomSheetDialog)
        }
    }

    fun onClickImageMenuItemType(type: DialogMenuItemType) {
        when(type) {
            DialogMenuItemType.CAMERA_IMAGE -> {
                cameraTempImageUri = imageUtil.getUriFromTempFileInExternalDir().also {
                    emitEventFlow(MyPageSettingEvent.GoToCamera(it))
                }
            }
            DialogMenuItemType.ALBUM_IMAGE -> emitEventFlow(MyPageSettingEvent.GoToAlbum)
            else -> defaultImage()
        }
    }

    private fun checkPermission(onSuccess:() -> Unit) {
        PermissionManager.createGetImagePermission(onSuccess)
    }

    fun onSelectImageFromAlbum(uri: Uri) {
        emitCropImageAndOptimizeEvent(uri)
    }

    fun onTakeImageFromCamera() {
        cameraTempImageUri?.let { uri ->
            emitCropImageAndOptimizeEvent(uri)
        }
    }

    private fun emitCropImageAndOptimizeEvent(uri: Uri) {
        emitEventFlow(
            MyPageSettingEvent.CropImageAndOptimize(
                imageUtil.getCropImageOptions(uri)
            )
        )
    }

    fun proceedCropImageResult(result: CropImageView.CropResult) {
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                val file = imageUtil.uriToOptimizeImageFile(uri)
                if (file != null) {
                    uploadProfileFile(file)
                }
            }
        }
    }

    fun onClickNicknameDeleteButton() {
        updateNickname("")
        onTextChangedAfter()
    }

    private fun defaultImage() {

    }

    private fun handleNicknameCheckSuccess(isAvailableNickname: Boolean) {
        isNetworkCall = false
        updateNicknameState(isAvailableNickname, R.string.sign_up_profile_compose_nickname_available_description)
    }

    private fun handleNicknameCheckError(nicknameError: NicknameError) {
        isNetworkCall = false
        when (nicknameError) {
            is NicknameError.HasSpecialCharacter -> updateNicknameState(false, R.string.sign_up_profile_compose_nickname_special_character_description)
            is NicknameError.HasBlankNickname -> updateNicknameState(false, R.string.sign_up_profile_compose_nickname_blank_description)
            is NicknameError.DuplicatedNickname -> updateNicknameState(false, R.string.sign_up_profile_compose_nickname_duplicated_description)
            is NicknameError.EmptyNickname -> updateNicknameState(null, R.string.sign_up_profile_compose_nickname_empty_description)
            else -> Unit
        }
    }

    private fun updateNicknameState(isActiveNickname: Boolean?, nicknameDescriptionRes: Int) {
        updateUiState { uiState ->
            uiState.copy(
                isSaveButtonEnable = isSaveButtonEnable(isActiveNickname),
                nicknameIsActive = isActiveNickname,
                nicknameDescription = resourceProvider.getString(nicknameDescriptionRes)
            )
        }
    }

    private fun uploadProfileFile(file: File) {
        viewModelScope.launch {
            postUploadFileUseCase(UploadFileRequestVo(UploadFileType.PROFILE, file)).collect{
                inspectUiState(it, ::handleUploadImageSuccess)
            }
        }
    }

    private fun handleUploadImageSuccess(state : UploadFileResponseVo){
        updateUiState { uiState ->
            uiState.copy(
                profileImage = state.fileUrl
            )
        }
    }

    private fun updateNickname(nickname: String) {
        updateUiState { uiState ->
            uiState.copy(
                nickname = nickname
            )
        }
    }

    private fun isSaveButtonEnable(isActiveNickname:Boolean?): Boolean {
        return isActiveNickname == true && !isNetworkCall
    }

    fun onIntroChangedAfter() {
        val introduce: String = uiState.value.introduce
        updateIntroduceState(introduce, introduce.isNotEmpty())
    }

    private fun updateIntroduceState(introduce:String, isSaveButtonEnable : Boolean) {
        updateUiState { uiState ->
            uiState.copy(
                isSaveButtonEnable = isSaveButtonEnable,
                introduceCount = getIntroduceCountSpannableString(introduce.length.toString())
            )
        }
    }

    private fun getIntroduceCountSpannableString(introduceLength: String): SpannableString {
        val introduceCountString = resourceProvider.getString(R.string.sign_up_more_info_introduce_count,introduceLength)
        val introduceCountColor = resourceProvider.getColor(R.color.color_363636)
        return SpannableString(introduceCountString).apply {
            setSpan(ForegroundColorSpan(introduceCountColor),0,introduceLength.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}