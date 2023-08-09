package com.plub.presentation.ui.sign.signup.authentication

import com.plub.presentation.ui.Event

sealed class GetPhoneEvent : Event {
    object MoveToEnd : GetPhoneEvent()
    object ShowBottomSheet : GetPhoneEvent()
}