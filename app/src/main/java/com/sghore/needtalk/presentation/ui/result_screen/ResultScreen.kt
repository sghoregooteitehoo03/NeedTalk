package com.sghore.needtalk.presentation.ui.result_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.model.UserTalkResult
import com.sghore.needtalk.presentation.ui.BaselineTextField
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.ExperiencePointBar
import com.sghore.needtalk.presentation.ui.FriendshipPointBar
import com.sghore.needtalk.presentation.ui.ProfileImage
import com.sghore.needtalk.util.parseMinuteSecond

@Composable
fun ResultScreen(
    uiState: ResultUiState,
    onEvent: (ResultUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colors.onPrimary
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "결과",
                        style = MaterialTheme.typography.h5.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                }
                SetTalkTitleLayout(
                    modifier = Modifier.padding(14.dp),
                    fileSize = uiState.fileSize,
                    title = uiState.talkTitle,
                    onChangeTitle = { onEvent(ResultUiEvent.ChangeTalkTitle(it)) }
                )
                Spacer(modifier = Modifier.height(32.dp))
                repeat(uiState.otherUsers.size) { index ->
                    if (uiState.otherUsers[index] != null) {
                        val friend = uiState.otherUsers[index]!!
                        FriendshipResult(
                            modifier = Modifier.padding(14.dp),
                            friend = friend,
                            talkResult = uiState.userTalkResult[index],
                            isNotFriend = friend.friendshipPoint == -1,
                            onAddFriend = { userId ->
                                onEvent(ResultUiEvent.AddFriend(userId, index))
                            }
                        )
                    }
                }
            }
            DefaultButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(14.dp),
                text = "확인",
                onClick = {
                    onEvent(ResultUiEvent.ClickConfirm)
                }
            )
        }
    }
}

@Composable
fun SetTalkTitleLayout(
    modifier: Modifier = Modifier,
    fileSize: String,
    title: String,
    onChangeTitle: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (fileSize.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_noize),
                    contentDescription = "Record",
                    tint = MaterialTheme.colors.secondary
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(4.dp),
                    text = fileSize,
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray)
                    )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
        BaselineTextField(
            hint = "대화제목을 지정해주세요.",
            text = title,
            onValueChange = onChangeTitle,
            maxTextLength = 30
        )
    }
}

// TODO: feat: 포인트 증가 기능 구현하기
@Composable
fun FriendshipResult(
    modifier: Modifier = Modifier,
    friend: UserData,
    talkResult: UserTalkResult,
    isNotFriend: Boolean,
    onAddFriend: (String) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large
            )
            .height(106.dp)
            .padding(14.dp)
    ) {
        val (profileImage, info, result) = createRefs()
        ProfileImage(
            modifier = Modifier
                .constrainAs(profileImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
            backgroundSize = 56.dp,
            imageSize = 46.dp,
            profileImage = friend.profileImage
        )
        Column(
            modifier = Modifier.constrainAs(info) {
                top.linkTo(parent.top)
                start.linkTo(profileImage.end, margin = 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = friend.name,
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                if (isNotFriend) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "친구 추가하여 친밀도를 올려보세요!",
                        style = MaterialTheme.typography.subtitle1.copy(
                            color = MaterialTheme.colors.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(
                    modifier = Modifier.alpha(
                        if (isNotFriend) {
                            0.3f
                        } else {
                            1f
                        }
                    )
                ) {
                    ExperiencePointBar(experiencePoint = friend.experiencePoint)
                    Spacer(modifier = Modifier.height(6.dp))
                    FriendshipPointBar(friendshipPoint = friend.friendshipPoint)
                }
            }
        }
        Row(
            modifier = Modifier
                .constrainAs(result) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        ) {
            if (isNotFriend) {
                IconWithText(
                    icon = painterResource(id = R.drawable.ic_add_friend),
                    iconTint = MaterialTheme.colors.secondary,
                    text = "친구추가",
                    textStyle = MaterialTheme.typography.subtitle1.copy(
                        color = MaterialTheme.colors.secondary
                    ),
                    onClick = { onAddFriend(friend.userId) }
                )
            } else {
                IconWithText(
                    icon = painterResource(id = R.drawable.ic_exp),
                    iconTint = colorResource(id = R.color.gray),
                    text = "+${talkResult.experiencePoint}",
                    textStyle = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray)
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconWithText(
                    icon = painterResource(id = R.drawable.ic_clock),
                    iconTint = colorResource(id = R.color.gray),
                    text = parseMinuteSecond(talkResult.talkTime),
                    textStyle = MaterialTheme.typography.subtitle1.copy(
                        color = colorResource(id = R.color.gray)
                    )
                )
            }
        }
    }
}

@Composable
fun IconWithText(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconTint: Color,
    text: String,
    textStyle: TextStyle,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = icon,
            contentDescription = text,
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = text,
            style = textStyle
        )
    }
}