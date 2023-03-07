package com.plub.presentation.ui.main.plubing.adapter

import androidx.recyclerview.widget.RecyclerView
import com.plub.domain.model.vo.plub.PlubingMemberInfoVo
import com.plub.presentation.R
import com.plub.presentation.databinding.IncludeItemMemberBinding
import com.plub.presentation.util.GlideUtil

class PlubingMemberViewHolder(
    private val binding: IncludeItemMemberBinding,
    private val listener: PlubingMemberAdapter.Delegate
) : RecyclerView.ViewHolder(binding.root) {

    private var vo: PlubingMemberInfoVo? = null

    init {
        binding.root.setOnClickListener {
            vo?.let {
                listener.onClickProfile(it.id)
            }
        }
    }

    fun bind(item: PlubingMemberInfoVo) {
        vo = item
        binding.apply {
            textViewNickname.text = item.nickname
            GlideUtil.loadImage(root.context,item.profileImage,imageViewProfile, R.drawable.iv_default_profile)
        }
    }
}