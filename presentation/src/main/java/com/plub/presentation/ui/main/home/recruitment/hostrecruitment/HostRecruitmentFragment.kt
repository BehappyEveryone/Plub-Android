package com.plub.presentation.ui.main.home.recruitment.hostrecruitment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.plub.domain.model.vo.home.recruitdetailvo.RecruitDetailJoinedAccountsListVo
import com.plub.presentation.base.BaseFragment
import com.plub.presentation.databinding.FragmentHostDetailRecruitmentPlubbingBinding
import com.plub.presentation.ui.main.home.recruitment.DetailRecruitPageState
import com.plub.presentation.ui.common.decoration.GridSpaceDecoration
import com.plub.presentation.ui.main.home.recruitment.adapter.DetailRecruitCategoryAdapter
import com.plub.presentation.ui.main.home.recruitment.adapter.DetailRecruitProfileAdapter
import com.plub.presentation.ui.main.home.recruitment.bottomsheet.ProfileBottomSheetFragment
import com.plub.presentation.util.px
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HostRecruitmentFragment :
    BaseFragment<FragmentHostDetailRecruitmentPlubbingBinding, DetailRecruitPageState, HostRecruitmentViewModel>(
        FragmentHostDetailRecruitmentPlubbingBinding::inflate
    ) {
    private val detailRecruitProfileAdapter: DetailRecruitProfileAdapter by lazy {
        DetailRecruitProfileAdapter(object : DetailRecruitProfileAdapter.DetailProfileDelegate {
            override fun onProfileClick(accountId: Int) {
                goToProfile(accountId)
            }

            override fun onSeeMoreProfileClick() {
                viewModel.openBottomSheet()
            }

        })
    }
    private val detailRecruitCategoryAdapter : DetailRecruitCategoryAdapter by lazy {
        DetailRecruitCategoryAdapter()
    }
    override val viewModel: HostRecruitmentViewModel by viewModels()

    override fun initView() {

        binding.apply {
            vm = viewModel
            //viewModel.fetchRecruitmentDetail(plubbingId = )
        }
    }

    override fun initStates() {
        repeatOnStarted(viewLifecycleOwner) {
            launch {
                viewModel.uiState.collect {
                    initDetailPage(it)
                }
            }
            launch {
                viewModel.eventFlow.collect{
                    inspectEvent(it as HostDetailPageEvent)
                }
            }
        }
    }

    private fun goToProfile(accountId: Int) {

    }

    private fun initDetailPage(data: DetailRecruitPageState) {
        binding.apply {
            constraintLayoutTop.bringToFront()
            detailRecruitCategoryAdapter.submitList(data.categories)
            recyclerViewPlubbingHobby.apply {
                layoutManager = GridLayoutManager(context, 4)
                addItemDecoration(GridSpaceDecoration(4, 8.px ,8.px,false))
                adapter = detailRecruitCategoryAdapter
            }

            detailRecruitProfileAdapter.submitList(data.joinedAccounts)
            recyclerViewPlubbingPeopleProfile.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = detailRecruitProfileAdapter
            }
        }
    }

    private fun inspectEvent(event : HostDetailPageEvent){
        when(event){
            is HostDetailPageEvent.GoToBack->{
                findNavController().popBackStack()
            }
            is HostDetailPageEvent.GoToEditFragment->{

            }
            is HostDetailPageEvent.GoToSeeApplicants->{

            }
            is HostDetailPageEvent.OpenBottomSheet -> {
                openProfileBottomSheet(event.joinedAccountsList)
            }
        }
    }

    private fun openProfileBottomSheet(joinedAccountList : List<RecruitDetailJoinedAccountsListVo>){
        val bottomSheet = ProfileBottomSheetFragment(joinedAccountList)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }
}