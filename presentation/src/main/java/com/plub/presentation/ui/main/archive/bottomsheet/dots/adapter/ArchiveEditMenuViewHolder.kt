package com.plub.presentation.ui.main.archive.bottomsheet.dots.adapter

import androidx.recyclerview.widget.RecyclerView
import com.plub.domain.model.enums.ArchiveMenuType
import com.plub.presentation.databinding.IncludeItemArchiveEditBinding

class ArchiveEditMenuViewHolder(
    private val binding: IncludeItemArchiveEditBinding,
    private val listener: ArchiveDotsMenuAdapter.ArchiveDotsMenuDelegate
) : RecyclerView.ViewHolder(binding.root) {

    var menuType : ArchiveMenuType? = null

    init {
        binding.root.setOnClickListener {
            menuType?.let { listener.onClick(it) }
        }
    }

    fun bind(type : ArchiveMenuType) {
        menuType = type
    }
}