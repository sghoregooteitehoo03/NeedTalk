package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.ExperiencePointBar
import com.sghore.needtalk.presentation.ui.FriendshipPointBar
import com.sghore.needtalk.presentation.ui.ProfileImage

@Composable
fun ProfileScreen(
    userData: UserData?,
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit
) {
    val testList = listOf(
        UserData(
            userId = "",
            name = "민머리",
            profileImage = userData!!.profileImage,
            experiencePoint = 80,
            friendshipPoint = 4
        ),
        UserData(
            userId = "",
            name = "대충 지은 이름",
            profileImage = userData.profileImage,
            experiencePoint = 50,
            friendshipPoint = 7
        ),
        UserData(
            userId = "",
            name = "닉네임",
            profileImage = userData.profileImage,
            experiencePoint = 13,
            friendshipPoint = 0
        )
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterStart)
                    .clickable { onEvent(ProfileUiEvent.ClickNavigateUp) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "navigateUp"
            )
        }
        LazyColumn(contentPadding = PaddingValues(14.dp)) {
            item {
                MyProfileLayout(
                    userData = userData,
                    onClickEditProfile = {
                        onEvent(ProfileUiEvent.ClickEditProfile)
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "친구 목록",
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (!uiState.isLoading && uiState.friends.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Text(
                            text = "친구 목록이 비어있습니다.",
                            style = MaterialTheme.typography.h4.copy(
                                color = MaterialTheme.colors.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "친구 목록이 비어있습니다.",
                            style = MaterialTheme.typography.h5.copy(
                                color = colorResource(id = R.color.gray)
                            )
                        )
                    }
                }
            } else {
                items(uiState.friends.size) { index ->
                    FriendInfoItem(
                        friend = uiState.friends[index],
                        onClickRemove = { onEvent(ProfileUiEvent.ClickRemoveFriend(it)) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MyProfileLayout(
    modifier: Modifier = Modifier,
    userData: UserData?,
    onClickEditProfile: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "내 프로필",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = MaterialTheme.shapes.large
                )
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (userData != null) {
                ProfileImage(
                    backgroundSize = 120.dp,
                    imageSize = 100.dp,
                    profileImage = userData.profileImage
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = userData.name,
                    style = MaterialTheme.typography.h4.copy(
                        color = MaterialTheme.colors.onSecondary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            color = colorResource(id = R.color.orange_80),
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable { onClickEditProfile() }
                        .padding(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "프로필 수정",
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
fun FriendInfoItem(
    modifier: Modifier = Modifier,
    friend: UserData,
    onClickRemove: (UserData) -> Unit
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
        val (profileImage, info, remove) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(profileImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    color = colorResource(id = R.color.light_gray_200),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = friend.profileImage,
                contentDescription = "ProfileImage",
                modifier = Modifier.size(46.dp)
            )
        }
        Column(
            modifier = Modifier.constrainAs(info) {
                top.linkTo(parent.top)
                start.linkTo(profileImage.end, margin = 16.dp)
                end.linkTo(remove.start, margin = 16.dp)
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
            ExperiencePointBar(experiencePoint = friend.experiencePoint)
            Spacer(modifier = Modifier.height(6.dp))
            FriendshipPointBar(friendshipPoint = friend.friendshipPoint)
        }
        Icon(
            modifier = Modifier
                .constrainAs(remove) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onClickRemove(friend) },
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "RemoveFriend",
            tint = MaterialTheme.colors.onPrimary
        )
    }
}