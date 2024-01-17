package com.sghore.needtalk.presentation.ui.create_screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Orange50
import com.sghore.needtalk.presentation.ui.theme.Orange80

@Composable
fun CreateScreen(
    uiState: CreateUiState,
    onEvent: (CreateUiEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                    .clickable { onEvent(CreateUiEvent.ClickBackArrow) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp",
                tint = MaterialTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        onEvent(CreateUiEvent.ClickComplete)
                    },
                text = "완료",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
        if (uiState.userEntity != null) {
            OptionLayout(
                modifier = Modifier.fillMaxWidth(),
                optionTitle = "대화시간 설정"
            ) {
                SetTimer(
                    currentTime = uiState.talkTime,
                    onTimeChange = { time -> onEvent(CreateUiEvent.ChangeTime(time)) },
                    isStopwatch = uiState.isStopwatch
                )
                Spacer(modifier = Modifier.height(8.dp))
                OptionItemWithSwitch(
                    text = "스톱워치 모드",
                    isChecked = uiState.isStopwatch,
                    onCheckedChange = { isAllow -> onEvent(CreateUiEvent.ClickStopWatchMode(isAllow)) }
                )
            }
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(id = R.color.light_gray),
                thickness = 8.dp
            )
            OptionLayout(
                modifier = Modifier.fillMaxWidth(),
                optionTitle = "음악"
            ) {
                MusicSelectPager(
                    musics = uiState.musics,
                    initialMusicId = uiState.initialMusicId,
                    changeInitialMusicId = { musicId ->
                        onEvent(CreateUiEvent.ChangeInitialMusicId(musicId))
                    },
                    onRemoveClick = { music ->
                        onEvent(CreateUiEvent.ClickRemoveMusic(music))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OptionItem(
                    text = "음악 추가하기",
                    onClick = { onEvent(CreateUiEvent.ClickAddMusic) }
                )
                OptionItemWithSwitch(
                    text = "음악 반복",
                    subText = "음악이 끝나도 계속해서 반복됩니다.",
                    isChecked = uiState.allowRepeatMusic,
                    onCheckedChange = { isAllow ->
                        onEvent(
                            CreateUiEvent.ClickAllowRepeatMusic(
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
            OptionLayout(
                modifier = Modifier.fillMaxWidth(),
                optionTitle = "인원 수"
            ) {
                SelectNumberOfPeople(
                    modifier = Modifier.fillMaxWidth(),
                    numberOfPeople = uiState.numberOfPeople,
                    onClickNumber = { number -> onEvent(CreateUiEvent.ClickNumberOfPeople(number)) }
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
        modifier = modifier
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

// TODO: 레이아웃만 클릭 되게 끔
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
                                        color = if (isStopwatch) {
                                            Orange80
                                        } else {
                                            Orange50
                                        },
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

// TODO: 사이즈 커지고 작아지는 애니메이션 추가
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicSelectPager(
    modifier: Modifier = Modifier,
    musics: List<MusicEntity>,
    initialMusicId: String,
    changeInitialMusicId: (String) -> Unit,
    onRemoveClick: (MusicEntity) -> Unit
) {
    val initialIndex = remember { musics.map { it.id }.indexOf(initialMusicId) }
    val pagerState = rememberPagerState(
        initialPage = if (initialIndex < 0) 0 else initialIndex,
        pageCount = { musics.size }
    )
    val currentPage = pagerState.currentPage
    val maxWidth = LocalConfiguration.current.screenWidthDp

    if (musics.isNotEmpty()) {
        changeInitialMusicId(musics[currentPage].id)
    }
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(
            start = (maxWidth.dp - 140.dp) / 2,
            end = (maxWidth.dp - 140.dp) / 2
        ),
        verticalAlignment = Alignment.Bottom
    ) { index ->
        MusicItem(
            modifier = Modifier.width(140.dp),
            thumbnailImage = musics[index].thumbnailImage,
            musicTitle = musics[index].title,
            isSelected = index == currentPage,
            onRemoveClick = {
                onRemoveClick(musics[index])
            }
        )
    }
}

@Composable
fun MusicItem(
    modifier: Modifier = Modifier,
    thumbnailImage: String,
    musicTitle: String,
    isSelected: Boolean,
    onRemoveClick: () -> Unit
) {
    Column(
        modifier = modifier
            .alpha(if (isSelected) 1f else 0.4f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (thumbnailImage.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(if (isSelected) 140.dp else 110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(80.dp),
                    painter = painterResource(id = R.drawable.ic_no_music),
                    contentDescription = "NoMusic",
                    tint = Color.White
                )
            }
        } else {
            Box {
                Image(
                    modifier = Modifier
                        .size(if (isSelected) 140.dp else 110.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    painter = rememberAsyncImagePainter(
                        model = thumbnailImage
                    ),
                    contentDescription = thumbnailImage,
                    contentScale = ContentScale.Crop
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .clip(CircleShape)
                            .background(
                                color = Color.White.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                            .size(20.dp)
                            .clickable {
                                onRemoveClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "RemoveMusic",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = musicTitle,
            style = if (isSelected) {
                MaterialTheme.typography.body1
                    .copy(color = MaterialTheme.colors.onPrimary)
            } else {
                MaterialTheme.typography.body2
                    .copy(color = MaterialTheme.colors.onPrimary)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// TODO: 클릭 효과 없애기, 레이아웃만 클릭 되게 끔
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

// time이 step값에 따라 값이 증가할 수 있도록 값을 보강 및 감소해주는 역할을 수행하는 함수
private fun getTimerTimeByStep(time: Long, stepTime: Long): Long {
    if (stepTime == 0L) {
        return time
    }

    val decimal = time % stepTime
    val necessaryValue = stepTime - decimal

    return if (decimal == 0L) {
        time
    } else {
        if (decimal > stepTime / 2000) {
            time + necessaryValue
        } else {
            time - decimal
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
fun MusicSelectPagerPreview() {
    NeedTalkTheme {
        val testList = listOf(
            MusicEntity(
                id = "1",
                thumbnailImage = "",
                "음악 1",
                timestamp = System.currentTimeMillis()
            ),
            MusicEntity(
                id = "2",
                thumbnailImage = "",
                "음악 2",
                timestamp = System.currentTimeMillis()
            ),
            MusicEntity(
                id = "3",
                thumbnailImage = "",
                "음악 3",
                timestamp = System.currentTimeMillis()
            )
        )
        MusicSelectPager(
            musics = testList,
            initialMusicId = "2",
            changeInitialMusicId = {},
            onRemoveClick = {}
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