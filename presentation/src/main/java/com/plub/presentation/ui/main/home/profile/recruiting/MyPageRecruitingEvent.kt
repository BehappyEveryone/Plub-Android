package com.plub.presentation.ui.main.home.profile.recruiting

import com.plub.presentation.ui.Event


sealed class MyPageRecruitingEvent : Event {
    data class GoToRecruit(val plubbingId : Int) : MyPageRecruitingEvent()
}