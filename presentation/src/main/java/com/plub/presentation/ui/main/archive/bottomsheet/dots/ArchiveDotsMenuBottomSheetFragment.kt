package com.plub.presentation.ui.main.archive.bottomsheet.dots

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.plub.domain.model.enums.ArchiveAccessType
import com.plub.domain.model.enums.ArchiveMenuType
import com.plub.presentation.base.BaseBottomSheetFragment
import com.plub.presentation.databinding.BottomSheetArchiveDotsMenuBinding
import com.plub.presentation.ui.main.archive.bottomsheet.dots.adapter.ArchiveDotsMenuAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArchiveDotsMenuBottomSheetFragment(
    private val plubbingId : Int,
    private val archiveId : Int,
    private val archiveAccessType: ArchiveAccessType,
    private val listener : ArchiveDotsDelegate) : BaseBottomSheetFragment<BottomSheetArchiveDotsMenuBinding, ArchiveDotsMenuBottomSheetState, ArchiveDotsMenuBottomSheetViewModel>(
    BottomSheetArchiveDotsMenuBinding::inflate
) {
    interface ArchiveDotsDelegate{
        fun onDelete()
        fun onClickEdit()
        fun onClickReport()
    }

    override val viewModel: ArchiveDotsMenuBottomSheetViewModel by viewModels()

    private val archiveDotsMenuAdapter : ArchiveDotsMenuAdapter by lazy {
        ArchiveDotsMenuAdapter(object : ArchiveDotsMenuAdapter.ArchiveDotsMenuDelegate{
            override fun onClick(archiveMenuType: ArchiveMenuType) {
                viewModel.onClickEvent(archiveMenuType)
            }
        })
    }

    override fun initView() {
        binding.apply {
            vm = viewModel

            recyclerViewMenuDots.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = archiveDotsMenuAdapter
            }
        }

        viewModel.setMenu(archiveAccessType)
        viewModel.setId(plubbingId, archiveId)
    }

    override fun initStates() {
        repeatOnStarted(viewLifecycleOwner){
            launch {
                viewModel.uiState.collect{
                    archiveDotsMenuAdapter.submitList(it.typeList)
                }
            }

            launch {
                viewModel.eventFlow.collect{
                    inspectEventFlow(it as ArchiveDotsMenuBottomSheetEvent)
                }
            }
        }
    }

    private fun inspectEventFlow(event : ArchiveDotsMenuBottomSheetEvent){
        when(event){
            ArchiveDotsMenuBottomSheetEvent.GoToReport -> listener.onClickReport()
            ArchiveDotsMenuBottomSheetEvent.DeleteArchive -> listener.onDelete()
            ArchiveDotsMenuBottomSheetEvent.EditArchive -> listener.onClickEdit()
        }
        dismiss()
    }
}