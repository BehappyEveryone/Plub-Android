package com.plub.presentation.ui.main.home.bookmark

import com.plub.presentation.ui.Event

sealed class BookmarksEvent : Event {
    object ScrollToTop : BookmarksEvent()
}