package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkTopicCategory
import com.sghore.needtalk.presentation.ui.theme.Green50

@Composable
fun AddTalkTopicScreen(
    uiState: AddTalkTopicUiState,
    onEvent: (AddTalkTopicUiEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .clickable { onEvent(AddTalkTopicUiEvent.ClickNavigateBack) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "navigateBack",
                tint = MaterialTheme.colors.onPrimary
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Complete",
                tint = MaterialTheme.colors.onPrimary
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            InputTalkTopic(
                talkTopicText = uiState.talkTopicText,
                onValueChange = { onEvent(AddTalkTopicUiEvent.ChangeTalkTopicText(it)) }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(1f)
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp)
        ) {
            SelectCategories(
                selectedCategories = uiState.selectedCategories,
                onSelectCategory = { onEvent(AddTalkTopicUiEvent.ClickTalkTopicCategory(it)) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            SetPublicAvailability(
                isPublic = uiState.isPublic,
                onClick = { onEvent(AddTalkTopicUiEvent.ClickSetPublic) }
            )
        }
    }
}

@Composable
fun InputTalkTopic(
    modifier: Modifier = Modifier,
    talkTopicText: String,
    onValueChange: (String) -> Unit,
    maxTextLength: Int = 100
) {
    val underlineColor = if (talkTopicText.isNotEmpty()) {
        MaterialTheme.colors.onPrimary
    } else {
        colorResource(id = R.color.gray)
    }

    Column(
        modifier = modifier.padding(start = 14.dp, end = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (talkTopicText.isEmpty()) {
                Text(
                    modifier = Modifier
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height - strokeWidth / 2 + (4.dp.toPx())
                            drawLine(
                                color = underlineColor,
                                start = Offset(0f, y),
                                end = Offset(size.width + 6.dp.toPx(), y),
                                strokeWidth = strokeWidth
                            )
                        },
                    text = "대화주제를 제작해보세요.",
                    style = MaterialTheme.typography.h4.copy(
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.gray)
                    )
                )
            }
            BasicTextField(
                modifier = Modifier
                    .drawBehind {
                        if (talkTopicText.isNotEmpty()) {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height - strokeWidth / 2 + (4.dp.toPx())
                            drawLine(
                                color = underlineColor,
                                start = Offset(0f, y),
                                end = Offset(size.width + 6.dp.toPx(), y),
                                strokeWidth = strokeWidth
                            )
                        }
                    },
                value = talkTopicText,
                onValueChange = {
                    if (it.length <= maxTextLength) {
                        onValueChange(it)
                    }
                },
                textStyle = MaterialTheme.typography.h4.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 8,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${talkTopicText.length}/$maxTextLength",
            style = MaterialTheme.typography.body1.copy(
                color = colorResource(id = R.color.gray)
            )
        )
    }
}

@Composable
fun SelectCategories(
    modifier: Modifier = Modifier,
    selectedCategories: List<TalkTopicCategory>,
    onSelectCategory: (TalkTopicCategory) -> Unit
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.minus(48).dp
    val width = maxWidth.div(3)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Friend,
                isSelected = isSelectedCategory(
                    selectedCategories,
                    TalkTopicCategory.Friend
                ),
                onClick = { onSelectCategory(TalkTopicCategory.Friend) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Couple,
                isSelected = isSelectedCategory(
                    selectedCategories,
                    TalkTopicCategory.Couple
                ),
                onClick = { onSelectCategory(TalkTopicCategory.Couple) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Family,
                isSelected = isSelectedCategory(
                    selectedCategories,
                    TalkTopicCategory.Family
                ),
                onClick = { onSelectCategory(TalkTopicCategory.Family) }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Balance,
                isSelected = isSelectedCategory(
                    selectedCategories,
                    TalkTopicCategory.Balance
                ),
                onClick = { onSelectCategory(TalkTopicCategory.Balance) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.SmallTalk,
                isSelected = isSelectedCategory(
                    selectedCategories,
                    TalkTopicCategory.SmallTalk
                ),
                onClick = { onSelectCategory(TalkTopicCategory.SmallTalk) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.DeepTalk,
                isSelected = isSelectedCategory(
                    selectedCategories,
                    TalkTopicCategory.DeepTalk
                ),
                onClick = { onSelectCategory(TalkTopicCategory.DeepTalk) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    talkTopicCategory: TalkTopicCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = if (isSelected) {
                    colorResource(id = R.color.light_orange)
                } else {
                    MaterialTheme.colors.background
                },
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() }
            .padding(top = 12.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = talkTopicCategory.iconRes),
            contentDescription = "CategoryImage"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = talkTopicCategory.title,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun SetPublicAvailability(
    modifier: Modifier = Modifier,
    isPublic: Boolean,
    onClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(2.dp, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = if (isPublic) {
                        MaterialTheme.colors.background
                    } else {
                        colorResource(id = R.color.light_gray_200)
                    },
                    shape = MaterialTheme.shapes.medium
                )
                .clickable { onClick() }
                .padding(start = 12.dp, end = 12.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = if (isPublic) {
                    "공개"
                } else {
                    "비공개"
                },
                style = MaterialTheme.typography.h5
            )
            if (isPublic) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.ic_unlock),
                    contentDescription = "PublicIcon",
                    tint = Green50
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = "NonPublicIcon",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (isPublic) {
            Text(
                text = "제작해주신 대화주제는 검토 후 업로드 될 예정입니다.",
                style = MaterialTheme.typography.body1.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
        }
    }
}

private fun isSelectedCategory(
    selectedCategories: List<TalkTopicCategory>,
    baseCategory: TalkTopicCategory
) = selectedCategories.any { it == baseCategory }