package com.plub.presentation.ui.main.plubing.schedule.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plub.domain.model.vo.schedule.ScheduleVo
import com.plub.presentation.R
import com.plub.presentation.databinding.BottomSheetScheduleDetailBinding
import com.plub.presentation.ui.common.decoration.GridSpaceDecoration
import com.plub.presentation.util.TimeFormatter
import com.plub.presentation.util.px
import com.plub.presentation.util.serializable
import com.plub.presentation.util.setInVisibleWithAnimation
import com.plub.presentation.util.setOnRecyclerViewClickListener
import com.plub.presentation.util.setVisibleWithAnimation
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetScheduleDetail : BottomSheetDialogFragment() {

    private val binding: BottomSheetScheduleDetailBinding by lazy {
        BottomSheetScheduleDetailBinding.inflate(layoutInflater)
    }

    private val MAX_FOLD_COLUMN: Int
        get() = (getScreenWidth() - PADDING_HORIZONTAL.px) / PROFILE_FOLD_WIDTH.px

    private val MAX_EXPAND_COLUMN: Int
        get() = (getScreenWidth() - PADDING_HORIZONTAL.px) / PROFILE_EXPAND_WIDTH.px

    private val foldProfileAdapter: FoldProfileAdapter by lazy {
        FoldProfileAdapter(MAX_FOLD_COLUMN)
    }

    private val expandProfileAdapter: ExpandProfileAdapter by lazy {
        ExpandProfileAdapter()
    }

    companion object {
        private const val SCHEDULE_VO = "SCHEDULE_VO"
        private const val PROFILE_FOLD_WIDTH = 50
        private const val PROFILE_EXPAND_WIDTH = 74
        private const val PADDING_HORIZONTAL = 32
        private const val ITEM_SPAN_SIZE = 1
        private const val PROFILE_FOLD_ITEM_SPACE = 4
        private const val PROFILE_EXPAND_ITEM_SPACE = 6


        fun newInstance(
            scheduleVo: ScheduleVo,
            okButtonClickEvent: ((calendarId: Int) -> Unit)? = null,
            noButtonClickEvent: ((calendarId: Int) -> Unit)? = null
        ) = BottomSheetScheduleDetail().apply {
            this.okButtonClickEvent = okButtonClickEvent
            this.noButtonClickEvent = noButtonClickEvent
            arguments = Bundle().apply {
                putSerializable(SCHEDULE_VO, scheduleVo)
            }
        }
    }

    private val scheduleVo: ScheduleVo by lazy {
        arguments?.serializable(SCHEDULE_VO) ?: ScheduleVo()
    }
    private var okButtonClickEvent: ((calendarId: Int) -> Unit)? = null
    private var noButtonClickEvent: ((calendarId: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.peekHeight = 250.px
        behavior.isHideable = true
        behavior.state = STATE_COLLAPSED

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    STATE_EXPANDED -> {
                        setLayoutExpanded()
                    }

                    STATE_COLLAPSED -> {
                        setLayoutCollapsed()
                    }

                    STATE_DRAGGING -> {
                        setLayoutDragging()
                    }
                }

            }
        })

        binding.apply {
            textViewTitle.text = scheduleVo.title
            setTextViewMonth(textViewMonth, scheduleVo)
            textViewDate.text = getTextViewDate(scheduleVo)
            textViewTime.text = getTextViewTime(scheduleVo)
            setLocation(textViewLocation, imageViewLocation, scheduleVo)

            setRecyclerViewAttendFold(behavior)
            setRecyclerViewAttendExpand(behavior)

            buttonYes.setOnClickListener {
                okButtonClickEvent?.let { it(scheduleVo.calendarId) }
                dismiss()
            }

            buttonNo.setOnClickListener {
                noButtonClickEvent?.let { it(scheduleVo.calendarId) }
                dismiss()
            }
        }
    }

    private fun setRecyclerViewAttendExpand(behavior: BottomSheetBehavior<View>) {
        binding.apply {
            recyclerViewAttendExpand.apply {
                layoutManager = GridLayoutManager(context, MAX_EXPAND_COLUMN).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return ITEM_SPAN_SIZE
                        }
                    }
                }
                addItemDecoration(
                    GridSpaceDecoration(
                        MAX_EXPAND_COLUMN,
                        PROFILE_EXPAND_ITEM_SPACE.px,
                        PROFILE_EXPAND_ITEM_SPACE.px,
                        false
                    )
                )
                adapter = expandProfileAdapter

                setOnRecyclerViewClickListener {
                    if (isVisible) {
                        setLayoutDragging()
                        behavior.state = STATE_COLLAPSED
                    }
                }
            }
        }

        val expandProfileList =
            scheduleVo.calendarAttendList.calendarAttendList
        expandProfileAdapter.submitList(expandProfileList)
    }

    private fun setRecyclerViewAttendFold(behavior: BottomSheetBehavior<View>) {
        binding.apply {
            recyclerViewAttendFold.apply {
                layoutManager = GridLayoutManager(context, MAX_FOLD_COLUMN).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return ITEM_SPAN_SIZE
                        }
                    }
                }
                addItemDecoration(
                    GridSpaceDecoration(
                        MAX_FOLD_COLUMN,
                        PROFILE_FOLD_ITEM_SPACE.px,
                        PROFILE_FOLD_ITEM_SPACE.px,
                        false
                    )
                )
                adapter = foldProfileAdapter

                setOnRecyclerViewClickListener {
                    if (isVisible) {
                        setLayoutDragging()
                        behavior.state = STATE_EXPANDED
                    }
                }
            }
        }

        val foldProfileList =
            scheduleVo.calendarAttendList.calendarAttendList.map { it.profileImage }
        foldProfileAdapter.submitList(foldProfileList)
    }

    private fun setLayoutDragging() {
        binding.apply {
            recyclerViewAttendFold.visibility = View.INVISIBLE
            recyclerViewAttendExpand.visibility = View.INVISIBLE
            buttonNo.visibility = View.INVISIBLE
            buttonYes.visibility = View.INVISIBLE
            buttonNo.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = recyclerViewAttendExpand.id
            }
        }
    }

    private fun setLayoutCollapsed() {
        binding.apply {
            recyclerViewAttendFold.setVisibleWithAnimation()
            recyclerViewAttendExpand.visibility = View.INVISIBLE
            buttonNo.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = binding.recyclerViewAttendFold.id
            }
            buttonNo.setVisibleWithAnimation()
            buttonYes.setVisibleWithAnimation()
        }
    }

    private fun setLayoutExpanded() {
        binding.apply {
            recyclerViewAttendFold.visibility = View.INVISIBLE
            recyclerViewAttendExpand.setVisibleWithAnimation()
            buttonNo.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = recyclerViewAttendExpand.id
            }
            buttonNo.setVisibleWithAnimation()
            buttonYes.setVisibleWithAnimation()
        }
    }

    private fun setTextViewMonth(textViewMonth: TextView, item: ScheduleVo) {
        val month = TimeFormatter.getIntMonthFromyyyyDashmmDashddFormat(item.startedAt)
        textViewMonth.text =
            binding.root.context.getString(R.string.word_birth_month, month.toString())
    }

    private fun getTextViewDate(item: ScheduleVo): String {
        val day = TimeFormatter.getStringDayFromyyyyDashmmDashddFormat(item.startedAt)
        return binding.root.context.getString(R.string.word_birth_day, day)
    }

    private fun getTextViewTime(item: ScheduleVo): String {
        val startTime = TimeFormatter.get_ah_colon_mm(item.startTime)
        val endTime = TimeFormatter.get_ah_colon_mm(item.endTime)
        return binding.root.context.getString(R.string.word_middle_hyphen, startTime, endTime)
    }

    private fun setLocation(
        textViewLocation: TextView,
        imageViewLocation: ImageView,
        item: ScheduleVo
    ) {
        if (item.placeName.isEmpty()) {
            textViewLocation.visibility = View.GONE
            imageViewLocation.visibility = View.GONE
        } else textViewLocation.text = item.placeName
    }

    private fun getScreenWidth(): Int {
        val display = requireActivity().applicationContext?.resources?.displayMetrics
        return display?.widthPixels ?: 0
    }
}