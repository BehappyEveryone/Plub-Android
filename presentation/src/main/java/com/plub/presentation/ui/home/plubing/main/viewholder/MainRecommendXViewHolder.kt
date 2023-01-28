package com.plub.presentation.ui.home.plubing.main.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.plub.presentation.databinding.IncludeItemLayoutMainRecommendGatheringNoChoiceBinding
import com.plub.presentation.ui.home.plubing.main.adapter.MainRecommendGatheringAdapter

class MainRecommendXViewHolder(
    private val binding: IncludeItemLayoutMainRecommendGatheringNoChoiceBinding,
    private val listener: MainRecommendGatheringAdapter.MainRecommendGatheringDelegate
): RecyclerView.ViewHolder(binding.root){

    init {
        binding.constraintLayoutRegisterInterest.setOnClickListener {
            listener.onClickRegister()
        }
    }

    fun bind() {
        binding.apply {

        }
    }
}