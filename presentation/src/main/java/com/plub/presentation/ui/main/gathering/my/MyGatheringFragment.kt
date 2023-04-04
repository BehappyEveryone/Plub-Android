package com.plub.presentation.ui.main.gathering.my

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.plub.presentation.R
import com.plub.presentation.base.BaseTestFragment
import com.plub.presentation.databinding.FragmentMyGatheringBinding
import com.plub.presentation.ui.common.custom.MyGatheringLinearLayoutManager
import com.plub.presentation.ui.main.gathering.my.adapter.MyGatheringsAdapter
import com.plub.presentation.util.PowerMenuUtil
import com.plub.presentation.util.onThrottleClick
import com.plub.presentation.util.px
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs


@AndroidEntryPoint
class MyGatheringFragment :
    BaseTestFragment<FragmentMyGatheringBinding, MyGatheringPageState, MyGatheringViewModel>(
        FragmentMyGatheringBinding::inflate
    ) {

    companion object {
        private const val VIEW_PAGER2_PADDING = 30
    }

    override val viewModel: MyGatheringViewModel by viewModels()
    private val myGatheringsAdapter: MyGatheringsAdapter by lazy {
        MyGatheringsAdapter(
            onMyGatheringMeatBallClick = { view, plubbingId ->
                PowerMenuUtil.getPlubPowerMenu(
                    requireContext(),
                    viewLifecycleOwner
                ) { _, _ ->

                }.showAsDropDown(view, (-59).px, 0)
            },
            onMyHostingMeatBallClick = { view, plubbingId ->
                PowerMenuUtil.getPlubPowerMenu(
                    requireContext(),
                    viewLifecycleOwner
                ) {
                        _, _ ->
                }.showAsDropDown(view, (-59).px, 0)
            },
            onContentClick = { plubbingId ->
                viewModel.goToPlubingMain(plubbingId)
            },
            onCreateGatheringClick = {
                viewModel.goToCreate()
            },
            onParticipateGatheringClick = {
                viewModel.goToHome()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getMyParticipatingGatherings()
    }

    override fun initView() {
        binding.apply {
            vm = viewModel

            viewPager2.apply {
                adapter = myGatheringsAdapter
                ovalDotsIndicator.attachTo(this)

                children.forEach { child ->
                    if (child is RecyclerView) {
                        child.apply {
                            setPadding(VIEW_PAGER2_PADDING.px, 0, VIEW_PAGER2_PADDING.px, 0)
                            clipToPadding = false
                        }
                        return@forEach
                    }
                }

                setPageTransformer { page, position ->
                    val normalizedPosition = abs(position)
                    page.findViewById<View>(R.id.view_shadow)?.let { view ->
                        view.alpha = normalizedPosition
                    }
                }
            }

            radioButtonMyGathering.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onClickRadioButtonMyGathering(isChecked)
            }

            radioButtonHost.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onClickRadioButtonHost(isChecked)
            }
        }
    }

    override fun initStates() {
        super.initStates()

        repeatOnStarted(viewLifecycleOwner) {
            launch {
                viewModel.uiState.myGatherings.collect {
                    myGatheringsAdapter.submitList(it)
                    binding.ovalDotsIndicator.setSingleItemIndicatorSize(it)
                }
            }

            launch {
                viewModel.uiState.myHostings.collect {
                    myGatheringsAdapter.submitList(it)
                    binding.ovalDotsIndicator.setSingleItemIndicatorSize(it)
                }
            }

            launch {
                viewModel.eventFlow.collect { event ->
                    if (event is MyGatheringEvent) inspectEventFlow(event)
                }
            }
        }
    }

    private fun inspectEventFlow(event: MyGatheringEvent) {
        when (event) {
            is MyGatheringEvent.GoToPlubingMain -> {
                val action =
                    MyGatheringFragmentDirections.actionMyGatheringToPlubingMain(event.plubingId)
                findNavController().navigate(action)
            }

            MyGatheringEvent.GoToCreateGathering -> {
                val action = MyGatheringFragmentDirections.actionMyGatheringToCreate()
                findNavController().navigate(action)
            }

            MyGatheringEvent.GoToPlubingHome -> {
                val action = MyGatheringFragmentDirections.actionMyGatheringToPlubingHome()
                findNavController().navigate(action)
            }
        }
    }

}