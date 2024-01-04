package com.sghore.needtalk.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Purple80

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
    textStyle: androidx.compose.ui.text.TextStyle
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
            modifier = Modifier.drawBehind {
                val strokeWidthPx = 2.dp.toPx()
                val verticalOffset = size.height - 2.sp.toPx()
                drawLine(
                    color = Color.Black,
                    strokeWidth = strokeWidthPx,
                    start = Offset(0f, verticalOffset),
                    end = Offset(size.width, verticalOffset)
                )
            },
            text = name,
            style = textStyle
        )
    }
}