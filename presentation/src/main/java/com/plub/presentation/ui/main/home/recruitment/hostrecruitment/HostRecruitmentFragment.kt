package com.plub.presentation.ui.main.home.recruitment.hostrecruitment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.plub.domain.model.vo.home.recruitdetailvo.RecruitDetailJoinedAccountsVo
import com.plub.presentation.base.BaseFragment
import com.plub.presentation.databinding.FragmentHostDetailRecruitmentPlubbingBinding
import com.plub.presentation.ui.main.home.recruitment.DetailRecruitPageState
import com.plub.presentation.ui.main.home.recruitment.adapter.DetailRecruitCategoryAdapter
import com.plub.presentation.ui.main.home.recruitment.adapter.DetailRecruitProfileAdapter
import com.plub.presentation.ui.main.home.recruitment.bottomsheet.ProfileBottomSheetFragment
import com.plub.presentation.util.GlideUtil
import com.plub.presentation.util.px
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HostRecruitmentFragment :
    BaseFragment<FragmentHostDetailRecruitmentPlubbingBinding, DetailRecruitPageState, HostRecruitmentViewModel>(
        FragmentHostDetailRecruitmentPlubbingBinding::inflate
    ) {

    companion object{
        private const val PROFILE_WIDTH = 42
    }

    private val detailRecruitProfileAdapter: DetailRecruitProfileAdapter by lazy {
        DetailRecruitProfileAdapter(object : DetailRecruitProfileAdapter.DetailProfileDelegate {
            override fun onProfileClick(accountId: Int) {
                viewModel.goToProfile(accountId)
            }

            override fun onSeeMoreProfileClick() {
                viewModel.openBottomSheet()
            }

        })
    }
    private val detailRecruitCategoryAdapter : DetailRecruitCategoryAdapter by lazy {
        DetailRecruitCategoryAdapter()
    }

    private val hostRecruitmentFragmentArgs : HostRecruitmentFragmentArgs by navArgs()
    override val viewModel: HostRecruitmentViewModel by viewModels()

    override fun initView() {

        binding.apply {
            vm = viewModel
            initRecycler()
            viewModel.fetchRecruitmentDetail(hostRecruitmentFragmentArgs.plubbingId)
        }
    }

    private fun initRecycler(){
        binding.apply {
            recyclerViewPlubbingHobby.apply {
                layoutManager = FlexboxLayoutManager(context)
                adapter = detailRecruitCategoryAdapter
            }
            recyclerViewPlubbingPeopleProfile.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = detailRecruitProfileAdapter
            }
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

    private fun initDetailPage(data: DetailRecruitPageState) {
        binding.apply {
            val maxProfile = recyclerViewPlubbingPeopleProfile.width / PROFILE_WIDTH.px
            GlideUtil.loadImage(root.context, data.plubbingMainImage, imageViewPlubbingImage)
            imageViewPlubbingImage.clipToOutline = true
            detailRecruitCategoryAdapter.submitList(data.categories)
            detailRecruitProfileAdapter.setMaxProfile(maxProfile)
            detailRecruitProfileAdapter.submitList(data.joinedAccounts)
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
            is HostDetailPageEvent.GoToProfile ->{

            }
        }
    }

    private fun openProfileBottomSheet(joinedAccountList : List<RecruitDetailJoinedAccountsVo>){
        val bottomSheet = ProfileBottomSheetFragment(joinedAccountList)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }
}