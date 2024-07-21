package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen.talk_highlight_dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R

@Composable
fun TalkHighlightDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "클립 목록",
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .clickable { onDismiss() },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close"
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 6.dp, end = 6.dp)
            ) {
                items(1) {
                    TalkHighlightItem()
                }
            }
        }
    }
}

@Composable
fun TalkHighlightItem(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colors.background, MaterialTheme.shapes.medium)
            .padding(14.dp)
    ) {
        val (title, seek, playBtn, btnLayout) = createRefs()
        Text(
            modifier = Modifier.constrainAs(title) {
                start.linkTo(parent.start)
                end.linkTo(playBtn.start, 20.dp)
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            },
            text = "하이라이트 제목",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        MediaSeekbar(
            modifier = Modifier.constrainAs(seek) {
                start.linkTo(parent.start)
                end.linkTo(playBtn.start, 20.dp)
                top.linkTo(title.bottom, 10.dp)
                width = Dimension.fillToConstraints
            }
        )
        Box(
            modifier = Modifier
                .constrainAs(playBtn) {
                    top.linkTo(title.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(seek.bottom)
                }
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp),
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "Play",
                tint = MaterialTheme.colors.onSecondary
            )
        }
        ItemMoreButtons(
            modifier = Modifier.constrainAs(btnLayout) {
                top.linkTo(seek.bottom, 24.dp)
                start.linkTo(parent.start)
            }
        )
    }
}

@Composable
fun MediaSeekbar(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        var canvasMaxWidth by remember { mutableFloatStateOf(0f) }
        var thumbPos by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val circleColor = colorResource(id = R.color.orange_80)
            val barColor = MaterialTheme.colors.secondary

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = CircleShape
                    )
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .onGloballyPositioned { canvasMaxWidth = it.size.width.toFloat() }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            thumbPos =
                                (thumbPos + dragAmount).coerceIn(0f, canvasMaxWidth)
                            change.consume()
                        }
                    }
            ) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(0f, 2.dp.toPx()),
                    size = Size(thumbPos, (size.height - 4.dp.toPx())),
                    cornerRadius = CornerRadius(100f, 100f)
                )
                drawCircle(
                    color = circleColor,
                    radius = 8.dp.toPx(),
                    center = Offset(thumbPos, size.height / 2)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "00:00",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = "01:00",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
        }
    }
}

@Composable
fun ItemMoreButtons(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = colorResource(id = R.color.light_gray),
                    shape = CircleShape
                )
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share",
                tint = MaterialTheme.colors.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = colorResource(id = R.color.light_gray),
                    shape = CircleShape
                )
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "Delete",
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}