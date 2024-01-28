package com.sghore.needtalk.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Red

@Composable
fun DisposableEffectWithLifeCycle(
    onCreate: () -> Unit = {},
    onResume: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    onDispose: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    onCreate()
                }

                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                }

                Lifecycle.Event.ON_STOP -> {
                    onStop()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    onDestroy()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            onDispose()
        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
    actions: @Composable (Modifier) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (contentRef, actionsRef) = createRefs()
        content(Modifier.constrainAs(contentRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
        })
        actions(Modifier.constrainAs(actionsRef) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        })
    }
}

@Composable
fun NameTag(
    modifier: Modifier = Modifier,
    name: String,
    color: Color,
    interval: Dp,
    colorSize: Dp,
    textStyle: TextStyle,
    isBorder: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .size(colorSize)
        ) {
            drawCircle(
                color = color,
                center = center
            )
        }
        Spacer(modifier = Modifier.width(interval))
        Text(
            modifier = if (isBorder) {
                Modifier.drawBehind {
                    val strokeWidthPx = 2.dp.toPx()
                    val verticalOffset = size.height
                    drawLine(
                        color = Color.Black,
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width - 3, verticalOffset)
                    )
                }
            } else {
                Modifier
            },
            text = name,
            style = textStyle
        )
    }
}

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    textStyle: TextStyle,
    paddingValues: PaddingValues,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    val enableColor = if (enable) {
        color
    } else {
        colorResource(id = R.color.light_gray)
    }

    Box(
        modifier = modifier
            .background(color = enableColor, shape = MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .clickable {
                if (enable) {
                    onClick()
                }
            }
            .padding(paddingValues)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = textStyle
        )
    }
}

@Preview
@Composable
fun NameTagPreview() {
    NeedTalkTheme {
        NameTag(
            name = "닉네임",
            color = Red,
            interval = 6.dp,
            colorSize = 16.dp,
            textStyle = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            isBorder = true
        )
    }
}