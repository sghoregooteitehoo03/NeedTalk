package com.sghore.needtalk.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.util.Constants

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
                val lineColor = MaterialTheme.colors.onPrimary
                Modifier.drawBehind {
                    val strokeWidthPx = 2.dp.toPx()
                    val verticalOffset = size.height
                    drawLine(
                        color = lineColor,
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

@Composable
fun AdmobBanner(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = Constants.AD_BANNER_TEST_ID

                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun DialogTalkTopics(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    topicCategory: String,
    talkTopics: List<TalkTopicEntity>,
    talkTopicItem: @Composable (TalkTopicEntity) -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .then(modifier)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = topicCategory,
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .rotate(90f)
                        .clickable { onDismiss() },
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(talkTopics) { talkTopicItem(it) }
            }
        }
    }
}

@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    hint: String,
    inputData: String,
    onDataChange: (String) -> Unit,
    maxLength: Int = 8
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = hint,
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${inputData.length}/${maxLength}",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputData,
            onValueChange = {
                if (it.length <= maxLength) {
                    onDataChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            maxLines = 1,
            textStyle = MaterialTheme.typography.h5,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onPrimary,
                backgroundColor = colorResource(id = R.color.light_gray),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.onPrimary
            )
        )
    }
}

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 54.dp,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.h5.copy(
        color = MaterialTheme.colors.onSecondary
    ),
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = if (isEnabled) {
                    MaterialTheme.colors.secondary
                } else {
                    colorResource(id = R.color.light_gray)
                },
                shape = MaterialTheme.shapes.medium
            )
            .then(
                if (isEnabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
fun TalkTopicCategoryTag(
    modifier: Modifier = Modifier,
    tagName: String,
    textStyle: TextStyle,
    paddingValues: PaddingValues
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = MaterialTheme.shapes.medium
            )
            .padding(paddingValues)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = tagName,
            style = textStyle
        )
    }
}