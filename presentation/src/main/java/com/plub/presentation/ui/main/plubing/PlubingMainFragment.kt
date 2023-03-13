package com.plub.presentation.ui.main.plubing

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.plub.domain.model.enums.PlubingBoardWriteType
import com.plub.domain.model.enums.PlubingMainPageType
import com.plub.presentation.R
import com.plub.presentation.base.BaseFragment
import com.plub.presentation.databinding.FragmentPlubingMainBinding
import com.plub.presentation.databinding.IncludeTabPlubingMainBinding
import com.plub.presentation.parcelableVo.ParsePlubingBoardVo
import com.plub.presentation.ui.main.plubing.adapter.FragmentPlubingMainPagerAdapter
import com.plub.presentation.ui.main.plubing.adapter.PlubingMemberAdapter
import com.plub.presentation.ui.main.plubing.board.write.BoardWriteFragment
import com.plub.presentation.util.getNavigationResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlubingMainFragment :
    BaseFragment<FragmentPlubingMainBinding, PlubingMainPageState, PlubingMainViewModel>(
        FragmentPlubingMainBinding::inflate
    ) {

    override val viewModel: PlubingMainViewModel by viewModels()
    private val plubingArgs: PlubingMainFragmentArgs by navArgs()

    private var pagerAdapter: FragmentPlubingMainPagerAdapter? = null

    private val memberListAdapter: PlubingMemberAdapter by lazy {
        PlubingMemberAdapter(object : PlubingMemberAdapter.Delegate {
            override fun onClickProfile(id: Int) {
                viewModel.onClickProfile(id)
            }
        })
    }

    override fun initView() {
        binding.apply {
            vm = viewModel

            pagerAdapter = FragmentPlubingMainPagerAdapter(this@PlubingMainFragment, plubingArgs.plubingId)
            viewPagerPlubMain.apply {
                adapter = pagerAdapter
                isUserInputEnabled = false
            }

            includeMainTop.recyclerViewMembers.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                adapter = memberListAdapter
            }

            includeMainHeader.imageViewArchiveCollapsed.setOnClickListener {
                findNavController().navigate(PlubingMainFragmentDirections.actionPlubingMainToArchive("조깅삭 아카이브", plubingArgs.plubingId))
            }

            appBarLayout.addOnOffsetChangedListener { appBarLayout, _ ->
                viewModel.onAppBarOffsetChanged(appBarLayout.y, appBarLayout.totalScrollRange)
            }

            TabLayoutMediator(tabLayoutPlubMain, viewPagerPlubMain) { tab, position ->
                tab.customView = getTabView(position)
            }.attach()
        }
        viewModel.initPlubingId(plubingArgs.plubingId)
        viewModel.onFetchPlubingMainInfo()


        binding.includeMainTop.textViewDate.setOnClickListener {
            val action = PlubingMainFragmentDirections.actionPlubingMainToSchedule(1, "123")
            findNavController().navigate(action)
        }
    }

    private val pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.onChangedPage(position)
        }
    }

    private fun getTabView(tabIndex: Int): View {
        return IncludeTabPlubingMainBinding.inflate(layoutInflater).apply {
            textViewTab.text = getTabTitleString(tabIndex)
        }.root
    }

    private fun getTabTitleString(tabIndex: Int): String {
        return when (PlubingMainPageType.valueOf(tabIndex)) {
            PlubingMainPageType.BOARD -> getString(R.string.plubing_main_tab_board)
            PlubingMainPageType.TODO_LIST -> getString(R.string.plubing_main_tab_todo_list)
        }
    }

    override fun initStates() {
        super.initStates()

        repeatOnStarted(viewLifecycleOwner) {
            launch {
                viewModel.uiState.collect {
                    memberListAdapter.submitList(it.memberList)
                }
            }

            launch {
                viewModel.eventFlow.collect {
                    inspectEventFlow(it as PlubingMainEvent)
                }
            }
        }

        getNavigationResult(BoardWriteFragment.KEY_RESULT_EDIT_COMPLETE) { vo: ParsePlubingBoardVo ->
            //
        }

        getNavigationResult(BoardWriteFragment.KEY_RESULT_CREATE_COMPLETE) { _:Unit ->
            //viewModel.onCompleteBoardCreate()
        }
    }

    private fun inspectEventFlow(event: PlubingMainEvent) {
        when (event) {
            is PlubingMainEvent.GoToWriteBoard -> {
                val action = PlubingMainFragmentDirections.actionPlubingMainToPlubingBoardWrite(writeType = PlubingBoardWriteType.CREATE)
                findNavController().navigate(action)
            }

            is PlubingMainEvent.GoToWriteTodo -> {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.viewPagerPlubMain.registerOnPageChangeCallback(pageChangeListener)
    }

    override fun onPause() {
        super.onPause()
        binding.viewPagerPlubMain.unregisterOnPageChangeCallback(pageChangeListener)
    }
}