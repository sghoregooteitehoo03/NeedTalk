package com.sghore.needtalk.domain.model

import com.sghore.needtalk.R

sealed class TalkTopicCategory(
    val code: Int,
    val title: String,
    val imageRes: Int,
    val iconRes: Int
) {
    data object Friend : TalkTopicCategory(
        code = 0,
        title = "친구",
        imageRes = R.drawable.friends,
        iconRes = R.drawable.ic_freind
    )

    data object Couple : TalkTopicCategory(
        code = 1,
        title = "애인",
        imageRes = R.drawable.couple,
        iconRes = R.drawable.ic_couple
    )

    data object Family : TalkTopicCategory(
        code = 2,
        title = "가족",
        imageRes = R.drawable.family,
        iconRes = R.drawable.ic_family
    )

    data object Balance : TalkTopicCategory(
        code = 3,
        title = "밸런스게임",
        imageRes = R.drawable.balance,
        iconRes = R.drawable.ic_balance
    )

    data object SmallTalk : TalkTopicCategory(
        code = 4,
        title = "스몰토크",
        imageRes = R.drawable.small_talk,
        iconRes = R.drawable.ic_small_talk
    )

    data object DeepTalk : TalkTopicCategory(
        code = 5,
        title = "깊은대화",
        imageRes = R.drawable.deep_talk,
        iconRes = R.drawable.ic_deep_talk
    )
}