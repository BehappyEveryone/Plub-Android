package com.plub.domain.usecase

import com.plub.domain.UiState
import com.plub.domain.base.UseCase
import com.plub.domain.model.vo.archive.ArchiveCardResponseVo
import com.plub.domain.repository.ArchiveRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllArchiveUseCase @Inject constructor(
    private val archiveRepository: ArchiveRepository
) :UseCase<Int, Flow<UiState<ArchiveCardResponseVo>>>(){
    override suspend fun invoke(request: Int): Flow<UiState<ArchiveCardResponseVo>> {
        return archiveRepository.fetchAllArchive(request)
    }
}