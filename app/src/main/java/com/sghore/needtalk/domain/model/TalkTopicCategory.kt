package com.sghore.needtalk.domain.model

import com.sghore.needtalk.R

sealed class TalkTopicCategory(val title: String, val imageRes: Int, val iconRes: Int) {
    data object Friend : TalkTopicCategory(
        title = "친구",
        imageRes = R.drawable.friends,
        iconRes = R.drawable.ic_freind
    )

    data object Couple : TalkTopicCategory(
        title = "애인",
        imageRes = R.drawable.couple,
        iconRes = R.drawable.ic_couple
    )

    data object Family : TalkTopicCategory(
        title = "가족",
        imageRes = R.drawable.family,
        iconRes = R.drawable.ic_family
    )

    data object Balance : TalkTopicCategory(
        title = "밸런스게임",
        imageRes = R.drawable.balance,
        iconRes = R.drawable.ic_balance
    )

    data object SmallTalk : TalkTopicCategory(
        title = "스몰토크",
        imageRes = R.drawable.small_talk,
        iconRes = R.drawable.ic_small_talk
    )

    data object DeepTalk : TalkTopicCategory(
        title = "깊은대화",
        imageRes = R.drawable.deep_talk,
        iconRes = R.drawable.ic_deep_talk
    )
}