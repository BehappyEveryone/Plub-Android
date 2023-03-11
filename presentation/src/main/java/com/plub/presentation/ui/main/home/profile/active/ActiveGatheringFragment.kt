package com.plub.presentation.ui.main.home.profile.active

import androidx.fragment.app.viewModels
import com.plub.presentation.base.BaseFragment
import com.plub.presentation.databinding.FragmentMyPageActiveGatheringBinding
import com.plub.presentation.ui.PageState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActiveGatheringFragment :
    BaseFragment<FragmentMyPageActiveGatheringBinding, PageState.Default, ActiveGatheringViewModel>(
        FragmentMyPageActiveGatheringBinding::inflate
    ) {


    override val viewModel: ActiveGatheringViewModel by viewModels()

    override fun initView() {
        binding.apply {
            vm = viewModel
        }

    }

    override fun initStates() {
        super.initStates()

        repeatOnStarted(viewLifecycleOwner) {
            launch {
                viewModel.uiState.collect {

                }
            }

            launch {
                viewModel.eventFlow.collect{
                }
            }
        }
    }
}