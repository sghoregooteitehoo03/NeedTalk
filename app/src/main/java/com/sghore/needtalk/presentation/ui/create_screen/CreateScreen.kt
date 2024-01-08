package com.sghore.needtalk.presentation.ui.create_screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlin.math.max
import kotlin.math.roundToInt

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
    modifier: Modifier = Modifier
) {
    var time by remember {
        mutableStateOf(0L)
    }
    var currentProgress by remember {
        mutableStateOf(0f)
    }
    var thumbPos by remember {
        mutableStateOf(0f)
    }
    val maxWidth = LocalConfiguration.current.screenWidthDp

    Column {
        Text(text = "progress: ${currentProgress}")
        Row(
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        thumbPos += (dragAmount.x / 2.5.dp.value)
                        thumbPos = thumbPos.coerceIn(0f, maxWidth.toFloat())

                        currentProgress = thumbPos / maxWidth
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val repeatTime = maxWidth / 8
            repeat(repeatTime + 1) { index ->
                Box(
                    modifier = modifier
                        .width(4.dp)
                            then (
                            if ((8.dp.value) * (index + 1) <= thumbPos) {
                                val randomDp = remember { (30..52).random().dp }
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

@Preview
@Composable
fun SetTimerPreview() {
    NeedTalkTheme {
        SetTimer()
    }
}


//@Composable
//fun LinearSlider(
//    value: Float,
//    onValueChange: (Float) -> Unit,
//    modifier: Modifier = Modifier,
//    range: ClosedFloatingPointRange<Float> = 0f..1f,
//    steps: Int = 100,
//    activeTrackColor: Color = Color(0xFF6200EE),
//    inactiveTrackColor: Color = Color(0xFFBDBDBD),
//    thumbColor: Color = Color(0xFF6200EE),
//    showValue: Boolean = true,
//) {
//    var currentValue by remember { mutableStateOf(value) }
//    var thumbPosition by remember { mutableStateOf(0f) }
//
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(48.dp)
//            .padding(horizontal = 16.dp)
//            .background(MaterialTheme.colorScheme.surface)
//            .pointerInput(Unit) {
//                detectTransformGestures { _, panGesture ->
//                    thumbPosition += panGesture.x
//                    thumbPosition = thumbPosition.coerceIn(0f, size.width)
//                    currentValue = calculateValue(thumbPosition, size.width, range)
//                    onValueChange(currentValue)
//                }
//            }
//    ) {
//        Canvas(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            drawLinearSlider(
//                size = size,
//                range = range,
//                activeTrackColor = activeTrackColor,
//                inactiveTrackColor = inactiveTrackColor
//            )
//
//            thumbPosition = calculateThumbPosition(currentValue, size.width, range)
//            drawThumb(thumbPosition, thumbColor)
//
//            if (showValue) {
//                drawValueText(currentValue, size.width, range)
//            }
//        }
//    }
//}
//
//@Composable
//private fun DrawScope.drawLinearSlider(
//    size: Size,
//    range: ClosedFloatingPointRange<Float>,
//    activeTrackColor: Color,
//    inactiveTrackColor: Color
//) {
//    val trackHeight = size.height / 4
//    val trackY = size.height / 2 - trackHeight / 2
//    val trackRect = Rect(0f, trackY, size.width, trackY + trackHeight)
//
//    drawRoundRect(
//        color = inactiveTrackColor,
//        size = trackRect.size,
//        cornerRadius = CornerRadius.Zero
//    )
//
//    val activeTrackWidth = calculateThumbPosition(range.endInclusive, size.width, range)
//    val activeTrackRect = Rect(0f, trackY, activeTrackWidth, trackY + trackHeight)
//
//    drawRoundRect(
//        color = activeTrackColor,
//        size = activeTrackRect.size,
//        cornerRadius = CornerRadius.Zero
//    )
//}
//
//@Composable
//private fun DrawScope.drawThumb(thumbPosition: Float, thumbColor: Color) {
//    val thumbRadius = 12.dp.toPx()
//    val thumbY = size.height / 2
//
//    drawCircle(
//        color = thumbColor,
//        center = Offset(thumbPosition, thumbY),
//        radius = thumbRadius,
//    )
//}
//
//@Composable
//private fun DrawScope.drawValueText(currentValue: Float, width: Float, range: ClosedFloatingPointRange<Float>) {
//    val textPadding = 8.dp.toPx()
//    val valueText = "%.2f".format(currentValue)
//    val textWidth = with(LocalDensity.current) {
//        Paint().apply {
//            this.textSize = 16.sp.toPx()
//        }.measureText(valueText)
//    }
//    val textX = calculateThumbPosition(currentValue, width, range) - textWidth / 2
//
//    drawRoundRect(
//        color = MaterialTheme.colorScheme.surface,
//        size = Size(textWidth + textPadding * 2, 28.dp.toPx()),
//        topLeft = Offset(textX - textPadding, size.height / 4 - 28.dp.toPx()),
//        cornerRadius = CornerRadius.Zero
//    )
//
//    drawIntoCanvas {
//        it.nativeCanvas.drawText(
//            text = valueText,
//            x = textX + textPadding,
//            y = size.height / 4,
//            Paint().apply {
//                this.color = MaterialTheme.colorScheme.onSurface
//                this.textSize = 16.sp.toPx()
//            }
//        )
//    }
//}
//
//@Composable
//fun calculateThumbPosition(value: Float, width: Float, range: ClosedFloatingPointRange<Float>): Float {
//    return (value - range.start) / (range.endInclusive - range.start) * width
//}
//
//@Composable
//fun calculateValue(thumbPosition: Float, width: Float, range: ClosedFloatingPointRange<Float>): Float {
//    val progress = thumbPosition / width
//    return range.start + progress * (range.endInclusive - range.start)
//}
//
//@Preview(showBackground = true)
//@Composable
//fun LinearSliderPreview() {
//    FeatherAndroidTasksTheme {
//        LinearSlider(
//            value = 0.5f,
//            onValueChange = {},
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//    }
//}