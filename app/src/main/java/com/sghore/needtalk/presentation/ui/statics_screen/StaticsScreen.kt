package com.sghore.needtalk.presentation.ui.statics_screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.ParticipantCount
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.presentation.ui.NameTag
import com.sghore.needtalk.presentation.ui.theme.Blue
import com.sghore.needtalk.presentation.ui.theme.Green
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Orange50
import com.sghore.needtalk.util.getFirstTime
import com.sghore.needtalk.util.getLastTime
import com.sghore.needtalk.util.getRandomColor
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StaticsScreen() {
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
                    .clickable { },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp",
                tint = MaterialTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "통계",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            DatePage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                baseDate = 0L,
                startDate = 0L,
                onLeftClick = {},
                onRightClick = {}
            )
            TotalTalkTime(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 14.dp),
                totalTime = 0L
            )
            Divider(
                color = colorResource(id = R.color.light_gray),
                thickness = 8.dp
            )
            WeekFocusTimeRate(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 14.dp, start = 14.dp, end = 14.dp),
                data = listOf(0, 3600000, 3600000, 7200000, 2452013, 7200000, 0)
            )
            Divider(
                color = colorResource(id = R.color.light_gray),
                thickness = 8.dp
            )
            TopParticipants(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 14.dp),
                participantCount = listOf(
                    ParticipantCount(
                        userEntity = UserEntity("bdsc", "거북이", color = Blue.toArgb()),
                        count = 12
                    )
                )
            )
            Divider(
                color = colorResource(id = R.color.light_gray),
                thickness = 8.dp
            )
            NumberOfPeopleRate(
                modifier = Modifier.padding(top = 24.dp, bottom = 14.dp),
                rate = listOf(60f, 20f, 20f)
            )
        }
    }
}

@Composable
fun DatePage(
    modifier: Modifier = Modifier,
    baseDate: Long,
    startDate: Long,
    onLeftClick: (Long) -> Unit,
    onRightClick: (Long) -> Unit
) {
    val currentTime = getFirstTime(System.currentTimeMillis())

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(24.dp)
                .clickable {
                    if (baseDate > startDate) {
                        onLeftClick(baseDate - 1000L)
                    }
                },
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = "",
            tint = if (baseDate == startDate)
                MaterialTheme.colors.onPrimary.copy(alpha = 0.4f)
            else
                MaterialTheme.colors.onPrimary
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = SimpleDateFormat("yy.MM.dd", Locale.KOREA).format(baseDate) + " ~ "
                    + SimpleDateFormat("yy.MM.dd", Locale.KOREA).format(getLastTime(baseDate)),
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    if (baseDate < currentTime) {
                        onRightClick(getLastTime(baseDate) + 1000L)
                    }
                },
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "",
            tint = if (baseDate == currentTime)
                MaterialTheme.colors.onPrimary.copy(alpha = 0.4f)
            else
                MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun TotalTalkTime(
    modifier: Modifier = Modifier,
    totalTime: Long
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = SimpleDateFormat(
                "H시간 m분",
                Locale.KOREA
            ).format(totalTime.minus(32400000)),
            style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.onPrimary)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "총 대화 한 시간",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary.copy(
                    alpha = 0.6f
                )
            )
        )
    }
}

@Composable
fun WeekFocusTimeRate(
    modifier: Modifier = Modifier,
    data: List<Long>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BarChart(
            modifier = Modifier.height(226.dp),
            data = data
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "요일별 대화한 시간",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary.copy(
                    alpha = 0.6f
                )
            )
        )
    }
}

@Composable
fun TopParticipants(
    modifier: Modifier = Modifier,
    participantCount: List<ParticipantCount>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            repeat(participantCount.size) { index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    NameTag(
                        name = participantCount[index].userEntity.name,
                        color = Color.Blue,
                        interval = 6.dp,
                        colorSize = 20.dp,
                        textStyle = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${participantCount[index].count}번",
                        style = MaterialTheme.typography.h3.copy(
                            fontSize = 32.sp,
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                }

                if (index < participantCount.size - 1)
                    Spacer(modifier = Modifier.width(42.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "주로 함께한 인원",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary.copy(
                    alpha = 0.6f
                )
            )
        )
    }
}

@Composable
fun NumberOfPeopleRate(
    modifier: Modifier = Modifier,
    rate: List<Float>
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutChart(
                modifier = Modifier.size(180.dp),
                data = rate
            )
            Spacer(modifier = Modifier.width(26.dp))
            Column {
                RateItem(
                    iconPainter = painterResource(id = R.drawable.ic_people_two),
                    rate = rate[0].toInt(),
                    color = getRandomColor(0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                RateItem(
                    iconPainter = painterResource(id = R.drawable.ic_people_three),
                    rate = rate[1].toInt(),
                    color = getRandomColor(1)
                )
                Spacer(modifier = Modifier.height(8.dp))
                RateItem(
                    iconPainter = painterResource(id = R.drawable.ic_people_four),
                    rate = rate[2].toInt(),
                    color = getRandomColor(2)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "인원 수 비율",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary.copy(
                    alpha = 0.6f
                )
            )
        )
    }
}

@Composable
fun RateItem(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    rate: Int,
    color: Color
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = iconPainter,
            contentDescription = "",
            tint = MaterialTheme.colors.onPrimary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${rate}%",
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
        )
        Spacer(modifier = Modifier.width(42.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    data: List<Float>
) {
    val totalValue by remember { mutableFloatStateOf(data.sum()) }

    Canvas(
        modifier = modifier
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2 - 19.dp.toPx()

        var startAngle = -90f
        data.forEachIndexed { index, value ->
            val sweepAngle = 360 * (value / totalValue)

            // Draw each section of the chart
            drawArc(
                color = getRandomColor(index), // Function to generate distinct colors
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(38.dp.toPx()),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<Long>
) {
    val maxDataValue by remember { mutableLongStateOf(data.max()) }
    val day = remember { listOf("일", "월", "화", "수", "목", "금", "토") }

    Box {
        ConstraintLayout(
            modifier = modifier.fillMaxWidth()
        ) {
            val (text0, text1, text2, text3, text4, text5, text6) = createRefs()
            val divider = remember { maxDataValue / 6 }
            val decimalFormat = DecimalFormat("#0")

            Text(
                modifier = Modifier.constrainAs(text0) { bottom.linkTo(parent.bottom) },
                text = "",
                style = MaterialTheme.typography.subtitle1
            )
            Row(
                modifier = Modifier.constrainAs(text1) {
                    bottom.linkTo(
                        text0.top,
                        ((((maxDataValue.toFloat() - divider * 6) / maxDataValue) * 220) + 11).dp
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(32.dp),
                    text = "${decimalFormat.format((maxDataValue - (maxDataValue / 5) * 5) / 60000L)}분",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray),
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Divider(
                    modifier = Modifier.clip(CircleShape),
                    color = colorResource(id = R.color.light_gray)
                )
            }
            Row(
                modifier = Modifier.constrainAs(text2) {
                    bottom.linkTo(
                        text0.top,
                        ((((maxDataValue.toFloat() - divider * 5) / maxDataValue) * 220) + 11).dp
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(32.dp),
                    text = "${decimalFormat.format((maxDataValue - (maxDataValue / 5) * 4) / 60000L)}분",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray),
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Divider(
                    modifier = Modifier.clip(CircleShape),
                    color = colorResource(id = R.color.light_gray)
                )
            }
            Row(
                modifier = Modifier.constrainAs(text3) {
                    bottom.linkTo(
                        text0.top,
                        ((((maxDataValue.toFloat() - divider * 4) / maxDataValue) * 220) + 11).dp
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(32.dp),
                    text = "${decimalFormat.format((maxDataValue - (maxDataValue / 5) * 3) / 60000L)}분",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray),
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Divider(
                    modifier = Modifier.clip(CircleShape),
                    color = colorResource(id = R.color.light_gray)
                )
            }
            Row(
                modifier = Modifier.constrainAs(text4) {
                    bottom.linkTo(
                        text0.top,
                        ((((maxDataValue.toFloat() - divider * 3) / maxDataValue) * 220) + 11).dp
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(32.dp),
                    text = "${decimalFormat.format((maxDataValue - (maxDataValue / 5) * 2) / 60000L)}분",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray),
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Divider(
                    modifier = Modifier.clip(CircleShape),
                    color = colorResource(id = R.color.light_gray)
                )
            }
            Row(
                modifier = Modifier.constrainAs(text5) {
                    bottom.linkTo(
                        text0.top,
                        ((((maxDataValue.toFloat() - divider * 2) / maxDataValue) * 220) + 11).dp
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(32.dp),
                    text = "${decimalFormat.format((maxDataValue - (maxDataValue / 5) * 1) / 60000L)}분",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray),
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Divider(
                    modifier = Modifier.clip(CircleShape),
                    color = colorResource(id = R.color.light_gray)
                )
            }
            Row(
                modifier = Modifier.constrainAs(text6) {
                    bottom.linkTo(
                        text0.top,
                        ((((maxDataValue.toFloat() - divider) / maxDataValue) * 220) + 11).dp
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(32.dp),
                    text = "${decimalFormat.format((maxDataValue) / 60000L)}분",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray),
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Divider(
                    modifier = Modifier.clip(CircleShape),
                    color = colorResource(id = R.color.light_gray)
                )
            }
        }
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Bottom
        ) {
            val width = LocalConfiguration.current.screenWidthDp - (28 + 36)
            val spacerWidth = (width - (12 * 7)) / 8

            Spacer(modifier = Modifier.width(30.dp))
            repeat(7) {
                Spacer(modifier = Modifier.width(spacerWidth.dp))

                val barHeight = if (data[it] == 0L) {
                    8f
                } else {
                    ((data[it].toFloat() / maxDataValue) * 191)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(barHeight.dp)
                            .clip(CircleShape)
                            .background(Orange50, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = day[it],
                        style = MaterialTheme.typography.subtitle1.copy(
                            color = MaterialTheme.colors.onPrimary.copy(
                                alpha = 0.6f
                            )
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DatePagePreview() {
    NeedTalkTheme {
        val currentTime = getFirstTime(System.currentTimeMillis())
        var baseDate by remember { mutableLongStateOf(getFirstTime(currentTime)) }

        DatePage(
            baseDate = baseDate,
            startDate = getFirstTime(1705923975000L),
            onLeftClick = { baseDate = getFirstTime(it) },
            onRightClick = { baseDate = getFirstTime(it) }
        )
    }
}

@Preview
@Composable
fun TotalTalkTimePreview() {
    NeedTalkTheme {
        val testList = listOf(
            TalkHistory(
                talkTime = (3600000L),
                users = listOf(
                    UserEntity("bdsc", "거북이", color = Blue.toArgb()),
                    UserEntity("hsef", "두루미", color = Green.toArgb())
                ),
                createTimeStamp = System.currentTimeMillis()
            ),
            TalkHistory(
                talkTime = (3600000L),
                users = listOf(
                    UserEntity("bdsc", "거북이", color = Blue.toArgb()),
                    UserEntity("hsef", "두루미", color = Green.toArgb())
                ),
                createTimeStamp = System.currentTimeMillis()
            )
        )

        TotalTalkTime(
            modifier = Modifier.padding(14.dp),
            totalTime = testList.sumOf { it.talkTime }
        )
    }
}

@Preview
@Composable
fun WeekFocusTimeRatePreview() {
    NeedTalkTheme {
        WeekFocusTimeRate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 14.dp, start = 14.dp, end = 14.dp),
            data = listOf(0, 3600000, 3600000, 7200000, 2452013, 7200000, 0)
        )
    }
}

@Preview
@Composable
fun TopParticipantsPreview() {
    NeedTalkTheme {
        TopParticipants(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 14.dp),
            participantCount = listOf(
                ParticipantCount(
                    userEntity = UserEntity("bdsc", "거북이", color = Blue.toArgb()),
                    count = 12
                )
            )
        )
    }
}

@Preview
@Composable
fun NumberOfPeopleRatePreview() {
    NeedTalkTheme {
        NumberOfPeopleRate(
            modifier = Modifier.padding(top = 14.dp, bottom = 14.dp),
            rate = listOf(60f, 20f, 20f)
        )
    }
}