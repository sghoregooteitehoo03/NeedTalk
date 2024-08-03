package com.sghore.needtalk.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.byteArrayToBitmap

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
fun AdmobBanner(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = Constants.AD_BANNER_ID

                loadAd(AdRequest.Builder().build())
            }
        }
    )
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
fun BaselineTextField(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    onValueChange: (String) -> Unit,
    maxTextLength: Int = 100,
    maxLine: Int = 8
) {
    val underlineColor = if (text.isNotEmpty()) {
        MaterialTheme.colors.onPrimary
    } else {
        colorResource(id = R.color.gray)
    }

    Column(
        modifier = modifier.padding(start = 14.dp, end = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (text.isEmpty()) {
                Text(
                    modifier = Modifier
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height - strokeWidth / 2 + (4.dp.toPx())
                            drawLine(
                                color = underlineColor,
                                start = Offset(0f, y),
                                end = Offset(size.width + 6.dp.toPx(), y),
                                strokeWidth = strokeWidth
                            )
                        },
                    text = hint,
                    style = MaterialTheme.typography.h4.copy(
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.gray)
                    )
                )
            }
            BasicTextField(
                modifier = Modifier
                    .drawBehind {
                        if (text.isNotEmpty()) {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height - strokeWidth / 2 + (4.dp.toPx())
                            drawLine(
                                color = underlineColor,
                                start = Offset(0f, y),
                                end = Offset(size.width + 6.dp.toPx(), y),
                                strokeWidth = strokeWidth
                            )
                        }
                    },
                value = text,
                onValueChange = {
                    if (it.length <= maxTextLength) {
                        onValueChange(it)
                    }
                },
                textStyle = MaterialTheme.typography.h4.copy(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = maxLine,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${text.length}/$maxTextLength",
            style = MaterialTheme.typography.body1.copy(
                color = colorResource(id = R.color.gray)
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
fun ConfirmWithCancelDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        ConstraintLayout(modifier = modifier) {
            val (topLayout, middleLayout, bottomLayout) = createRefs()
            Box(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(topLayout) {
                    top.linkTo(parent.top)
                }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = title,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(middleLayout) {
                        top.linkTo(topLayout.bottom, 16.dp)
                        bottom.linkTo(bottomLayout.top, 24.dp)
                    }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = message,
                    style = MaterialTheme.typography.h5.copy(
                        color = colorResource(id = R.color.gray)
                    ),
                    textAlign = TextAlign.Center
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomLayout) {
                    bottom.linkTo(parent.bottom)
                }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            color = colorResource(id = R.color.light_gray_200),
                            shape = MaterialTheme.shapes.medium
                        )
                        .height(44.dp)
                        .clickable {
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSecondary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            color = MaterialTheme.colors.secondary,
                            shape = MaterialTheme.shapes.medium
                        )
                        .height(44.dp)
                        .clickable {
                            onConfirm()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = confirmText,
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleInputDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    hint: String,
    startInputData: String,
    maxLength: Int = 15,
    buttonText: String,
    onButtonClick: (String) -> Unit
) {
    var inputData by remember { mutableStateOf(startInputData) }

    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(modifier = modifier) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = title,
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
            Spacer(modifier = Modifier.height(14.dp))
            DefaultTextField(
                hint = hint,
                inputData = inputData,
                onDataChange = { inputData = it },
                maxLength = maxLength
            )
            Spacer(modifier = Modifier.height(24.dp))
            DefaultButton(
                text = buttonText,
                buttonHeight = 46.dp,
                isEnabled = inputData.isNotEmpty(),
                onClick = {
                    onButtonClick(inputData)
                    onDismiss()
                }
            )
        }
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

@Composable
fun ExperiencePointBar(
    modifier: Modifier = Modifier,
    experiencePoint: Float,
    maxExperiencePoint: Float = Constants.MAX_EXPERIENCE_POINT
) {
    val progress = (experiencePoint / maxExperiencePoint)
    val maxWidth = 200.dp.value

    Box(
        modifier = modifier
            .width(maxWidth.dp)
            .clip(CircleShape)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = CircleShape
            )
            .height(12.dp)
    ) {
        Box(
            modifier = modifier
                .width((maxWidth * progress).dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = CircleShape
                )
                .height(12.dp)
                .fillMaxWidth(0.5f)
        )
    }
}

@Composable
fun FriendshipPointBar(
    modifier: Modifier = Modifier,
    friendshipPoint: Int,
    maxFriendshipPoint: Int = Constants.MAX_FRIENDSHIP_POINT
) {
    Row(modifier = modifier) {
        repeat(maxFriendshipPoint) { index ->
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(
                    id = if (index < friendshipPoint) {
                        R.drawable.filled_heart
                    } else {
                        R.drawable.unfilled_heart
                    }
                ),
                contentDescription = "FriendshipPoint"
            )
        }
    }
}

@Composable
fun TalkUserInfo(
    modifier: Modifier = Modifier,
    userData: UserData,
    isCurrentUser: Boolean
) {
    Row(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .then(
                if (isCurrentUser) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = MaterialTheme.shapes.medium
                    )
                } else {
                    Modifier
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    color = colorResource(id = R.color.light_gray_200),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = userData.profileImage,
                contentDescription = "ProfileImage",
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = userData.name,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
    }
}

@Composable
fun ParticipantInfoItem(
    modifier: Modifier = Modifier,
    participantInfo: ParticipantInfo,
    isCurrentUser: Boolean,
    isReady: Boolean?
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.medium
                )
                .then(
                    if (isCurrentUser) {
                        Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colors.secondary,
                            shape = MaterialTheme.shapes.medium
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        color = colorResource(id = R.color.light_gray_200),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = byteArrayToBitmap(participantInfo.profileImage).asImageBitmap(),
                    contentDescription = "ProfileImage",
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = participantInfo.name,
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary
                )
            )
        }
        if (isReady == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        color = colorResource(id = R.color.light_green),
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
    }
}

@Composable
fun EmptyTalkUserInfo(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart,
    content: @Composable (Modifier) -> Unit
) {
    Box(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = MaterialTheme.shapes.medium
            )
            .height(54.dp)
    ) {
        content(Modifier.align(alignment))
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    backgroundSize: Dp,
    imageSize: Dp,
    profileImage: ImageBitmap
) {
    Box(
        modifier = modifier
            .size(backgroundSize)
            .clip(CircleShape)
            .background(
                color = colorResource(id = R.color.light_gray_200),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = profileImage,
            contentDescription = "ProfileImage",
            modifier = Modifier.size(imageSize)
        )
    }
}