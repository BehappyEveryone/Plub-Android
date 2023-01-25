package com.plub.presentation.ui.home.plubing.categoryChoice

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.plub.presentation.state.SampleHomeState
import com.plub.presentation.base.BaseFragment
import com.plub.presentation.databinding.FragmentCategoryChoiceBinding
import com.plub.presentation.state.PageState
import com.plub.presentation.ui.home.adapter.MainRecommendAdapter
import com.plub.presentation.ui.home.adapter.MainRecommendGatheringAdapter
import com.plub.presentation.ui.home.adapter.MainRecommendGridAdapter
import com.plub.presentation.ui.home.plubing.main.MainFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryChoiceFragment : BaseFragment<FragmentCategoryChoiceBinding, PageState.Default, CategoryChoiceViewModel>(
    FragmentCategoryChoiceBinding::inflate
)  {
    private val categorygridAdapter : MainRecommendGridAdapter by lazy {
        MainRecommendGridAdapter(object : MainRecommendGridAdapter.MainRecommendGridDelegate {
            //TODO 리스너 달기
        })
    }
    private val categorylistAdapter : MainRecommendAdapter by lazy {
        MainRecommendAdapter(object : MainRecommendGatheringAdapter.MainRecommendGatheringDelegate {
            //TODO 리스너 달기
        })
    }

    private val categoryId : MainFragmentArgs by navArgs()
    override val viewModel: CategoryChoiceViewModel by viewModels()

    override fun initView() {

        binding.apply {
            vm = viewModel
            viewModel.fetchRecommendationGatheringData(categoryId.categoryId.toInt())
            //TODO 할 일
        }
    }

    override fun initStates() {
        super.initStates()
        repeatOnStarted(viewLifecycleOwner) {
            launch {
                viewModel.recommendationData.collect {
                    when(it.plubbings.content.size){
                        0 -> Log.d("TAG", "nothing")//HasNotDataRecycler()
                        else -> {
                            categorylistAdapter.submitList(it.plubbings.content)
                            setListRecycler()
                        }
                    }
                }
            }

        }
        repeatOnStarted(viewLifecycleOwner){
            launch {
                viewModel.switchList.collect{
                    when(it){
                        "그리드" -> changeGridRecycler()
                        "리스트" -> setListRecycler()
                    }
                }

            }
        }
        repeatOnStarted(viewLifecycleOwner){
            launch {
                viewModel.goToDetailRecruitmentFragment.collect{
                    goToDetailRecruitment()
                }

            }
        }
    }

    fun setListRecycler(){
        binding.recyclerViewCategoryChoiceList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categorylistAdapter
        }
    }

    fun changeGridRecycler(){
        binding.recyclerViewCategoryChoiceList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = categorygridAdapter
        }
    }

    fun goToDetailRecruitment(){
        val action = CategoryChoiceFragmentDirections.actionCategoryChoiceFragmentToRecruitmentFragment()
        findNavController().navigate(action)
    }
}