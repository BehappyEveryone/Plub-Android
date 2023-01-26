package com.plub.presentation.ui.home.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.plub.domain.model.vo.home.recommendationgatheringvo.RecommendationGatheringResponseContentListVo
import com.plub.presentation.R
import com.plub.presentation.databinding.IncludeItemRecommendGatheringListItemBinding
import com.plub.presentation.ui.home.adapter.MainRecommendGatheringAdapter

class MainRecommendListViewHolder(
    private val binding: IncludeItemRecommendGatheringListItemBinding,
    private val listener : MainRecommendGatheringAdapter.MainRecommendGatheringDelegate
): RecyclerView.ViewHolder(binding.root){
    private var bookmarkFlag = false
    private var plubbingId = 0

    init {
        binding.apply {
            constraintLayoutItemLayout.setOnClickListener {
                listener.onClickGoRecruitDetail(plubbingId)
            }

            imageBtnBookmark.setOnClickListener {
                if (bookmarkFlag) {
                    imageBtnBookmark.setImageResource(R.drawable.ic_unchecked_bookmark_white)
                } else {
                    imageBtnBookmark.setImageResource(R.drawable.ic_bookmark_checked)
                }
                bookmarkFlag = !bookmarkFlag
                listener.onClickBookmark(plubbingId)
            }
        }
    }

    fun bind(item: RecommendationGatheringResponseContentListVo) {
        bookmarkFlag = item.isBookmarked
        plubbingId = item.plubbingId
        binding.apply {
            //GlideUtil.loadImage(root.context, item.mainImage, imageViewMeet)
            if(bookmarkFlag){
                imageBtnBookmark.setImageResource(R.drawable.ic_bookmark_checked)
            }
            textViewMeetTitle.text = item.title
            textViewMeetOnelineIntroduce.text = item.introduce
            textViewLocation.text = item.roadAddress
            textViewPeople.text = "모집 인원 ${item.curAccountNum + item.remainAccountNum}명"
            textViewDate.text = "${item.days.toString()} | ${item.time}"
        }
    }
}