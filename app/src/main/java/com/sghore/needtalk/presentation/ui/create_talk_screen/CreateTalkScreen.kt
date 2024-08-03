package com.sghore.needtalk.presentation.ui.create_talk_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.AdmobBanner
import com.sghore.needtalk.presentation.ui.EmptyTalkUserInfo
import com.sghore.needtalk.presentation.ui.TalkUserInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CreateTalkScreen(
    userData: UserData?,
    uiState: CreateTalkUiState,
    onEvent: (CreateTalkUiEvent) -> Unit
) {
    if (!uiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.secondary)
        ) {
            val times = remember { (5..120 step 5).toList() }
            var currentIndex by remember {
                mutableIntStateOf(times.indexOf((uiState.talkTime / 60000).toInt()))
            }

            Box(
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = MaterialTheme.colors.secondary),
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterStart)
                        .clickable { onEvent(CreateTalkUiEvent.ClickBackArrow) },
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "navigateUp",
                    tint = MaterialTheme.colors.onSecondary
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            onEvent(
                                CreateTalkUiEvent.ClickComplete(
                                    times[currentIndex] * 60000L
                                )
                            )
                        },
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "navigateUp",
                    tint = MaterialTheme.colors.onSecondary
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.28f)
                    .padding(14.dp)
            ) {
                SetTalkTimeLayout(
                    times = times,
                    isEnabled = uiState.isTimer,
                    currentIndex = currentIndex,
                    isTimer = uiState.isTimer,
                    onChangeSelectedIndex = { currentIndex = it }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    TalkOptionsLayout(
                        isTimer = uiState.isTimer,
                        isAllowMic = uiState.isMicAllow,
                        onClickAllowTimer = { onEvent(CreateTalkUiEvent.ClickAllowTimer(it)) },
                        onClickAllowMic = { onEvent(CreateTalkUiEvent.ClickAllowMic(it)) }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    SetPeopleCountLayout(
                        userData = userData,
                        numberOfPeople = uiState.numberOfPeople,
                        onClickDecrease = { onEvent(CreateTalkUiEvent.ClickDecreasePeople) },
                        onClickIncrease = { onEvent(CreateTalkUiEvent.ClickIncreasePeople) }
                    )
                }
                AdmobBanner(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

@Composable
fun SetTalkTimeLayout(
    modifier: Modifier = Modifier,
    times: List<Int>,
    isEnabled: Boolean,
    currentIndex: Int,
    isTimer: Boolean,
    onChangeSelectedIndex: (Int) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(
                    if (isEnabled) {
                        1f
                    } else {
                        0.6f
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        color = colorResource(R.color.orange_80),
                        shape = MaterialTheme.shapes.medium
                    )
            )
            TalkTimePager(
                times = times,
                currentIndex = currentIndex,
                isEnabled = isTimer,
                onChangeSelectedIndex = onChangeSelectedIndex
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "대화 시간",
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.onSecondary,
                fontSize = 24.sp
            )
        )
    }
}

@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
@Composable
fun TalkTimePager(
    modifier: Modifier = Modifier,
    times: List<Int>,
    currentIndex: Int,
    isEnabled: Boolean = true,
    onChangeSelectedIndex: (Int) -> Unit
) {
    val localDensity = LocalDensity.current

    val itemWidth = with(localDensity) { 100.dp.toPx() }
    val animatable = remember { Animatable(currentIndex.toFloat()) }
    val decay = splineBasedDecay<Float>(localDensity)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isEnabled) {
                    Modifier.pointerInput(Unit) {
                        coroutineScope {
                            while (true) {
                                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                                val tracker = VelocityTracker()

                                animatable.stop()
                                awaitPointerEventScope {
                                    horizontalDrag(pointerId) { change ->
                                        tracker.addPosition(change.uptimeMillis, change.position)
                                        val horizontalDragOffset =
                                            animatable.value - change.positionChange().x / itemWidth

                                        Log.i("Check", "offset: $horizontalDragOffset")
                                        launch {
                                            val value = horizontalDragOffset.coerceIn(
                                                0f,
                                                (times.size - 1).toFloat()
                                            )

                                            onChangeSelectedIndex(value.toInt())
                                            animatable.snapTo(value)
                                        }
                                        change.consume()
                                    }
                                }

                                launch {
                                    val velocity = tracker.calculateVelocity().x / (times.size - 1)
                                    val targetValue =
                                        decay.calculateTargetValue(animatable.value, -velocity)
                                    val target = targetValue
                                        .roundToInt()
                                        .coerceIn(0, times.size - 1)
                                        .toFloat()

                                    onChangeSelectedIndex(target.toInt())
                                    animatable.animateTo(
                                        targetValue = target,
                                        initialVelocity = velocity
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        times.forEachIndexed { index, time ->
            val offsetX = (index - animatable.value) * itemWidth

            TalkTimeItem(
                modifier = Modifier.graphicsLayer {
                    translationX = offsetX
                },
                time = time,
                isSelected = currentIndex == index
            )
        }
    }
}

@Composable
fun TalkTimeItem(
    modifier: Modifier = Modifier,
    time: Int,
    isSelected: Boolean
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$time",
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.onSecondary.copy(
                    alpha = if (isSelected) {
                        1f
                    } else {
                        0.6f
                    }
                ),
                fontWeight = if (isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        )
    }
}

@Composable
fun TalkOptionsLayout(
    modifier: Modifier = Modifier,
    isTimer: Boolean,
    isAllowMic: Boolean,
    onClickAllowTimer: (Boolean) -> Unit,
    onClickAllowMic: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "옵션",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        TalkOptionItem(
            optionName = if (isTimer) {
                "타이머"
            } else {
                "스톱워치"
            },
            optionIcon = if (isTimer) {
                painterResource(id = R.drawable.ic_timer)
            } else {
                painterResource(id = R.drawable.ic_timer_off)
            },
            onClick = {
                onClickAllowTimer(!isTimer)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        TalkOptionItem(
            optionName = if (isAllowMic) {
                "녹음 허용"
            } else {
                "녹음 비허용"
            },
            optionIcon = if (isAllowMic) {
                painterResource(id = R.drawable.ic_mic)
            } else {
                painterResource(id = R.drawable.ic_mic_off)
            },
            onClick = {
                onClickAllowMic(!isAllowMic)
            }
        )
    }
}

@Composable
fun TalkOptionItem(
    modifier: Modifier = Modifier,
    optionName: String,
    optionIcon: Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = optionName,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Image(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp),
            painter = optionIcon,
            contentDescription = optionName
        )
    }
}

@Composable
fun SetPeopleCountLayout(
    modifier: Modifier = Modifier,
    userData: UserData?,
    numberOfPeople: Int,
    onClickDecrease: () -> Unit,
    onClickIncrease: () -> Unit,
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.minus(36).dp
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = "인원 수",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2)
        ) {
            item {
                if (userData != null) {
                    TalkUserInfo(
                        modifier = Modifier
                            .width(maxWidth.div(2))
                            .padding(4.dp),
                        userData = userData,
                        isCurrentUser = true
                    )
                }
            }
            item {
                EmptyTalkUserInfo(
                    modifier = Modifier
                        .width(maxWidth.div(2))
                        .padding(4.dp),
                    content = {}
                )
            }
            if (numberOfPeople > 2) {
                items(numberOfPeople - 2) {
                    DecreaseTalkUserInfo(
                        modifier = Modifier
                            .width(maxWidth.div(2))
                            .padding(4.dp),
                        onClickRemove = onClickDecrease
                    )
                }
            }
            if (numberOfPeople < 4) {
                item {
                    IncreaseTalkUserInfo(
                        modifier = Modifier
                            .width(maxWidth.div(2))
                            .padding(4.dp),
                        onClickAdd = onClickIncrease
                    )
                }
            }
        }
    }
}

@Composable
fun IncreaseTalkUserInfo(
    modifier: Modifier = Modifier,
    onClickAdd: () -> Unit
) {
    EmptyTalkUserInfo(
        modifier = modifier.clickable { onClickAdd() },
        alignment = Alignment.Center
    ) {
        Icon(
            modifier = it.size(28.dp),
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "increase",
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun DecreaseTalkUserInfo(
    modifier: Modifier = Modifier,
    onClickRemove: () -> Unit
) {
    EmptyTalkUserInfo(
        modifier = modifier,
        alignment = Alignment.TopEnd
    ) {
        Icon(
            modifier = it
                .padding(6.dp)
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onClickRemove() },
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "decrease",
            tint = MaterialTheme.colors.onPrimary
        )
    }
}