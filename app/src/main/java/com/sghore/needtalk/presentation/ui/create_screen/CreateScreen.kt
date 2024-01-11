package com.sghore.needtalk.presentation.ui.create_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Orange50
import java.text.DecimalFormat

@Composable
fun CreateScreen(

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
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp",
                tint = MaterialTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = "완료",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
        OptionLayout(
            modifier = Modifier.fillMaxWidth(),
            optionTitle = "대화시간 설정"
        ) {
            SetTimer(
                currentTime = 3600000L,
                onTimeChange = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            OptionItemWithSwitch(
                text = "스톱워치 모드",
                isChecked = false,
                onCheckedChange = {}
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
            OptionItem(
                text = "음악 추가하기"
            )
            OptionItemWithSwitch(
                text = "음악 반복",
                subText = "음악이 끝나도 계속해서 반복됩니다.",
                isChecked = false,
                onCheckedChange = {}
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
    subText: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
            subText = subText
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
    var progress = remember {
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
    var thumbPos by remember {
        mutableFloatStateOf(progress * maxWidth)
    }

    Column(
        modifier = modifier
            .padding(start = 14.dp, end = 14.dp)
            .alpha(
                if (isStopwatch) {
                    0.4f
                } else {
                    1f
                }
            )
    ) {
        val decimalFormat = DecimalFormat("#0")
        Box(
            modifier = Modifier
                .padding(
                    start = if (thumbPos.dp - 35.dp <= 0.dp) {
                        0.dp
                    } else if (thumbPos.dp - 35.dp >= maxWidth.dp - (54 + 28).dp) {
                        maxWidth.dp - (54 + 28).dp
                    } else {
                        thumbPos.dp - 35.dp
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
                    "${decimalFormat.format(currentTime)}분"
                },
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .pointerInput(Unit) {
                    if (!isStopwatch) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            thumbPos += dragAmount / 2.6f
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
                                ) / 60000
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
                            then (
                            if ((8.dp.value) * (index + 1) <= thumbPos) {
                                Modifier
                                    .height(randomDp)
                                    .background(color = Orange50, shape = CircleShape)
                            } else {
                                Modifier
                                    .height(26.dp)
                                    .background(
                                        color = colorResource(id = R.color.light_gray),
                                        shape = CircleShape
                                    )
                            })
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }

}

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
            mutableStateOf(3600000L)
        }
        SetTimer(
            currentTime = time,
            onTimeChange = {
                time = it
            },
            isStopwatch = false
        )
    }
}