package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
                    .align(Alignment.CenterStart),
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
            InputTalkTopic()
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(1f)
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp)
        ) {
            SelectCategories()
            Spacer(modifier = Modifier.height(24.dp))
            SetPublicAvailability()
        }
    }
}

@Composable
fun InputTalkTopic(
    modifier: Modifier = Modifier,
    maxTextLength: Int = 100
) {
    var text by remember {
        mutableStateOf("")
    }
    var isFocused by remember {
        mutableStateOf(false)
    }
    val underlineColor = if (text.isNotEmpty()) {
        MaterialTheme.colors.onPrimary
    } else {
        colorResource(id = R.color.gray)
    }

    Column(
        modifier = modifier.padding(start = 14.dp, end = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (text.isEmpty()) {
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
                        if (text.isNotEmpty()) {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height - strokeWidth / 2 + (4.dp.toPx())
                            drawLine(
                                color = underlineColor,
                                start = Offset(0f, y),
                                end = Offset(size.width + 6.dp.toPx(), y),
                                strokeWidth = strokeWidth
                            )
                        }
                    }
                    .onFocusChanged { isFocused = it.isFocused },
                value = text,
                onValueChange = {
                    if (it.length <= maxTextLength) {
                        text = it
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
            text = "${text.length}/$maxTextLength",
            style = MaterialTheme.typography.body1.copy(
                color = colorResource(id = R.color.gray)
            )
        )
    }
}

@Composable
fun SelectCategories(
    modifier: Modifier = Modifier
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
                talkTopicCategory = TalkTopicCategory.Friend
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Couple
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Family
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.Balance
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.SmallTalk
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                modifier = Modifier.width(width),
                talkTopicCategory = TalkTopicCategory.DeepTalk
            )
        }
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    talkTopicCategory: TalkTopicCategory
) {
    Column(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
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
fun SetPublicAvailability(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(2.dp, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(start = 12.dp, end = 12.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "공개",
                style = MaterialTheme.typography.h5
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.ic_unlock),
                contentDescription = "PublicIcon",
                tint = Green50
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "제작해주신 대화주제는 검토 후 업로드 될 예정입니다.",
            style = MaterialTheme.typography.body1.copy(
                color = colorResource(id = R.color.gray)
            )
        )
    }
}