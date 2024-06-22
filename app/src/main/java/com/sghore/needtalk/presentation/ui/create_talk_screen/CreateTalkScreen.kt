package com.sghore.needtalk.presentation.ui.create_talk_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdSize
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.presentation.ui.AdmobBanner
import com.sghore.needtalk.presentation.ui.RoundedButton
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.util.getTimerTimeByStep

@Composable
fun CreateScreen(
    uiState: CreateTalkUiState,
    onEvent: (CreateTalkUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(start = 14.dp, end = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .clickable { onEvent(CreateTalkUiEvent.ClickBackArrow) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp",
                tint = MaterialTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        onEvent(CreateTalkUiEvent.ClickComplete)
                    },
                text = "완료",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
            )
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            if (uiState.userEntity != null) {
                OptionLayout(optionTitle = "대화시간 설정") {
                    SetTimer(
                        currentTime = uiState.talkTime,
                        onTimeChange = { time -> onEvent(CreateTalkUiEvent.ChangeTime(time)) },
                        isStopwatch = uiState.isStopwatch
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OptionItemWithSwitch(
                        text = "스톱워치 모드",
                        isChecked = uiState.isStopwatch,
                        onCheckedChange = { isAllow ->
                            onEvent(
                                CreateTalkUiEvent.ClickStopWatchMode(
                                    isAllow
                                )
                            )
                        }
                    )
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(id = R.color.light_gray),
                    thickness = 8.dp
                )
                OptionLayout(optionTitle = "인원 수") {
                    SelectNumberOfPeople(
                        modifier = Modifier.fillMaxWidth(),
                        numberOfPeople = uiState.numberOfPeople,
                        onClickNumber = { number ->
                            onEvent(
                                CreateTalkUiEvent.ClickNumberOfPeople(
                                    number
                                )
                            )
                        }
                    )
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(id = R.color.light_gray),
                    thickness = 8.dp
                )
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    AdmobBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sizeIn(minHeight = 40.dp),
                        adSize = AdSize.LARGE_BANNER
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(id = R.color.light_gray),
                    thickness = 8.dp
                )
                Box {
                    OptionLayout(
                        modifier = Modifier.align(Alignment.CenterStart),
                        optionTitle = "대화 주제"
                    ) {}
                    Icon(
                        modifier = Modifier
                            .padding(end = 14.dp)
                            .clip(CircleShape)
                            .size(20.dp)
                            .align(Alignment.CenterEnd)
                            .clickable { onEvent(CreateTalkUiEvent.ClickAddTopic) },
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                TopicCategory(
                    onClickCategory = { topicCategory, groupCode ->
                        onEvent(CreateTalkUiEvent.ClickTopicCategory(topicCategory, groupCode))
                    }
                )
            }
        }
    }
}

@Composable
fun OptionLayout(
    modifier: Modifier = Modifier,
    optionTitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(start = 14.dp, top = 14.dp),
            text = optionTitle,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    text: String,
    subText: String = "",
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .height(54.dp)
            .padding(start = 14.dp, end = 14.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1
        )
        if (subText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                style = MaterialTheme.typography.subtitle2.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
        }
    }
}

@Composable
fun OptionItemWithSwitch(
    modifier: Modifier = Modifier,
    text: String,
    subText: String = "",
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(modifier = modifier) {
        OptionItem(
            modifier = Modifier.align(Alignment.CenterStart),
            text = text,
            subText = subText,
            onClick = { onCheckedChange(!isChecked) }
        )
        Switch(
            modifier = Modifier.align(Alignment.CenterEnd),
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SetTimer(
    modifier: Modifier = Modifier,
    currentTime: Long,
    maxTime: Long = 7200000L,
    stepTime: Long = 300000,
    isStopwatch: Boolean = false,
    onTimeChange: (Long) -> Unit
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp
    var progress = remember(isStopwatch) {
        if (isStopwatch) {
            maxTime.toFloat()
        } else {
            if ((currentTime / maxTime.toFloat()) < 0.025f) {
                (currentTime / maxTime.toFloat()) + 0.025f
            } else {
                (currentTime / maxTime.toFloat())
            }
        }
    }
    var thumbPos by remember(isStopwatch) {
        mutableFloatStateOf(progress * maxWidth)
    }

    Column(
        modifier = modifier
            .padding(start = 14.dp, end = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = if (thumbPos.dp - 36.dp <= 0.dp) {
                        0.dp
                    } else if (thumbPos.dp - 36.dp >= maxWidth.dp - (54 + 28).dp) {
                        maxWidth.dp - (54 + 28).dp
                    } else {
                        thumbPos.dp - 36.dp
                    }
                )
                .width(54.dp)
                .height(32.dp)
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = CircleShape
                ),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = if (isStopwatch) {
                    "∞"
                } else {
                    "${currentTime / 60000}분"
                },
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .pointerInput(isStopwatch) {
                    if (!isStopwatch) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            thumbPos += dragAmount / 2.5f
                            thumbPos = thumbPos.coerceIn(0f, maxWidth.toFloat())

                            progress = if (progress <= 0.025f) {
                                (thumbPos / maxWidth) + 0.025f
                            } else {
                                (thumbPos / maxWidth)
                            }

                            val time = (progress * maxTime).toLong()
                            onTimeChange(
                                getTimerTimeByStep(
                                    time = time,
                                    stepTime = stepTime
                                )
                            )
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val repeatTime = maxWidth / 8

            repeat(repeatTime + 1) { index ->
                val randomDp = remember(index) { (30..52).random().dp }
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .then(
                            if ((8.5.dp.value) * (index + 1) <= thumbPos) {
                                Modifier
                                    .height(randomDp)
                                    .background(
                                        color = MaterialTheme.colors.secondary,
                                        shape = CircleShape
                                    )
                            } else {
                                Modifier
                                    .height(26.dp)
                                    .background(
                                        color = colorResource(id = R.color.light_gray),
                                        shape = CircleShape
                                    )
                            }
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }

}

@Composable
fun SelectNumberOfPeople(
    modifier: Modifier = Modifier,
    numberOfPeople: Int,
    onClickNumber: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        SelectNumberOfPeopleItem(
            modifier = Modifier.size(80.dp),
            iconPainter = painterResource(id = R.drawable.ic_people_two),
            isSelected = numberOfPeople == 2,
            onClick = { onClickNumber(2) }
        )
        Spacer(modifier = Modifier.width(48.dp))
        SelectNumberOfPeopleItem(
            modifier = Modifier.size(80.dp),
            iconPainter = painterResource(id = R.drawable.ic_people_three),
            isSelected = numberOfPeople == 3,
            onClick = { onClickNumber(3) }
        )
        Spacer(modifier = Modifier.width(48.dp))
        SelectNumberOfPeopleItem(
            modifier = Modifier.size(80.dp),
            iconPainter = painterResource(id = R.drawable.ic_people_four),
            isSelected = numberOfPeople == 4,
            onClick = { onClickNumber(4) }
        )
    }
}

@Composable
fun SelectNumberOfPeopleItem(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable {
            if (!isSelected) {
                onClick()
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = modifier,
            painter = iconPainter,
            contentDescription = "",
            tint = if (isSelected) {
                MaterialTheme.colors.secondary
            } else {
                MaterialTheme.colors.onPrimary
            }
        )
        RadioButton(
            selected = isSelected,
            onClick = {
                if (!isSelected) {
                    onClick()
                }
            }
        )
    }
}

@Composable
fun TopicCategory(
    modifier: Modifier = Modifier,
    onClickCategory: (topicCategory: String, groupCode: Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp)
    ) {
        Row {
            TopicCategoryItem(
                title = "친구",
                backgroundImage = painterResource(id = R.drawable.friends),
                onClick = { onClickCategory("친구", 0) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TopicCategoryItem(
                title = "애인",
                backgroundImage = painterResource(id = R.drawable.couple),
                onClick = { onClickCategory("애인", 1) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            TopicCategoryItem(
                title = "가족",
                backgroundImage = painterResource(id = R.drawable.family),
                onClick = { onClickCategory("가족", 2) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TopicCategoryItem(
                title = "밸런스게임",
                backgroundImage = painterResource(id = R.drawable.balance),
                onClick = { onClickCategory("밸런스게임", 3) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            TopicCategoryItem(
                title = "스몰토크",
                backgroundImage = painterResource(id = R.drawable.small_talk),
                onClick = { onClickCategory("스몰토크", 4) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TopicCategoryItem(
                title = "깊은 대화",
                backgroundImage = painterResource(id = R.drawable.deep_talk),
                onClick = { onClickCategory("깊은 대화", 5) }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun TopicCategoryItem(
    modifier: Modifier = Modifier,
    title: String,
    backgroundImage: Painter,
    onClick: () -> Unit
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp

    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .width((maxWidth.dp / 2) - 18.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            Image(
                modifier = Modifier.matchParentSize(),
                painter = backgroundImage,
                contentDescription = title,
                contentScale = ContentScale.FillHeight
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
        )
    }
}

@Composable
fun TalkTopicItem(
    modifier: Modifier = Modifier,
    talkTopicEntity: TalkTopicEntity,
    onDeleteTopic: (TalkTopicEntity) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 54.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = talkTopicEntity.topic,
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        if (talkTopicEntity.createTime != 0L) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .clickable { onDeleteTopic(talkTopicEntity) },
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "DeleteTopic",
                tint = MaterialTheme.colors.onPrimary
            )
        }
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 2.dp,
            color = colorResource(id = R.color.light_gray)
        )
    }
}

@Composable
fun DialogAddTopic(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAddClick: (TalkTopicEntity) -> Unit
) {
    var talkTopic by remember { mutableStateOf("") }
    var selectedGroupCode by remember { mutableIntStateOf(0) }
    val maxWidth = LocalConfiguration.current.screenWidthDp

    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "대화주제 추가",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                value = talkTopic,
                onValueChange = { talkTopic = it },
                placeholder = {
                    Text(text = "대화주제 입력")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.light_gray),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                Row {
                    SelectTopicItem(
                        modifier = Modifier.width((maxWidth.dp / 3) - 12.dp),
                        text = "친구",
                        isSelected = selectedGroupCode == 0,
                        onClick = { selectedGroupCode = 0 }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SelectTopicItem(
                        modifier = Modifier.width((maxWidth.dp / 3) - 12.dp),
                        text = "애인",
                        isSelected = selectedGroupCode == 1,
                        onClick = { selectedGroupCode = 1 }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SelectTopicItem(
                        modifier = Modifier.width((maxWidth.dp / 3) - 12.dp),
                        text = "가족",
                        isSelected = selectedGroupCode == 2,
                        onClick = { selectedGroupCode = 2 }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    SelectTopicItem(
                        modifier = Modifier.width((maxWidth.dp / 3) - 12.dp),
                        text = "밸런스게임",
                        isSelected = selectedGroupCode == 3,
                        onClick = { selectedGroupCode = 3 }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SelectTopicItem(
                        modifier = Modifier.width((maxWidth.dp / 3) - 12.dp),
                        text = "스몰토크",
                        isSelected = selectedGroupCode == 4,
                        onClick = { selectedGroupCode = 4 }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SelectTopicItem(
                        modifier = Modifier.width((maxWidth.dp / 3) - 12.dp),
                        text = "진지한 대화",
                        isSelected = selectedGroupCode == 5,
                        onClick = { selectedGroupCode = 5 }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            RoundedButton(
                modifier = Modifier.fillMaxWidth(),
                text = "추가하기",
                color = MaterialTheme.colors.secondary,
                textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary),
                paddingValues = PaddingValues(14.dp),
                enable = talkTopic.isNotEmpty(),
                onClick = {
                    onAddClick(
                        TalkTopicEntity(
                            topic = talkTopic,
                            createTime = System.currentTimeMillis(),
                            groupCode = selectedGroupCode
                        )
                    )
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun SelectTopicItem(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected)
                    MaterialTheme.colors.secondary
                else
                    colorResource(id = R.color.light_gray_200),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            text = text,
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSecondary,
                fontWeight = FontWeight.Bold
            )
        )
        if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Check",
                tint = MaterialTheme.colors.onSecondary
            )
        }
    }
}

@Preview
@Composable
fun SetTimerPreview() {
    NeedTalkTheme {
        var time by remember {
            mutableLongStateOf(3600000L)
        }
        SetTimer(
            currentTime = time,
            onTimeChange = {
                time = it
            },
            isStopwatch = true
        )
    }
}

@Preview
@Composable
fun SelectNumberOfPeoplePreview() {
    var number by remember { mutableIntStateOf(2) }
    NeedTalkTheme {
        SelectNumberOfPeople(
            modifier = Modifier.fillMaxWidth(),
            numberOfPeople = number,
            onClickNumber = { number = it }
        )
    }
}

@Preview
@Composable
private fun TopicCategoryItemPreview() {
    NeedTalkTheme {
        val resource = R.drawable.phone_image
        TopicCategoryItem(
            title = "친구",
            backgroundImage = painterResource(id = resource),
            onClick = {}
        )
    }
}