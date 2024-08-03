package com.sghore.needtalk.presentation.ui.create_profile_screen

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.DefaultTextField
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.util.getBitmapFromResource

@SuppressLint("HardwareIds")
@Composable
fun CreateProfileScreen(
    uiState: CreateProfileUiState,
    onEvent: (CreateProfileUiEvent) -> Unit,
    faceImageResources: List<Int>,
    hairStyleImageResources: List<Int>,
    accessoryImageResources: List<Int>
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (uiState.isUpdateProfile) {
                    "프로필 수정"
                } else {
                    "프로필 생성"
                },
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selectedFaceImageRes = faceImageResources[uiState.selectedFaceIndex],
                    selectedHairImageRes = hairStyleImageResources[uiState.selectedHairStyleIndex],
                    selectedAccessoryImageRes = accessoryImageResources[uiState.selectedAccessoryIndex]
                )
                Spacer(modifier = Modifier.height(24.dp))
                DefaultTextField(
                    hint = "닉네임",
                    inputData = uiState.profileName,
                    onDataChange = { onEvent(CreateProfileUiEvent.ChangeName(it)) }
                )
                Spacer(modifier = Modifier.height(32.dp))
                SelectStyleImage(
                    styleTitle = "표정",
                    styleImageResources = faceImageResources,
                    selectedIndex = uiState.selectedFaceIndex,
                    onChangeSelectedIndex = {
                        onEvent(CreateProfileUiEvent.SelectProfileImage(ProfileType.Face, it))
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
                SelectStyleImage(
                    styleTitle = "머리스타일",
                    styleImageResources = hairStyleImageResources,
                    selectedIndex = uiState.selectedHairStyleIndex,
                    onChangeSelectedIndex = {
                        onEvent(CreateProfileUiEvent.SelectProfileImage(ProfileType.Hair, it))
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
                SelectStyleImage(
                    styleTitle = "악세서리",
                    styleImageResources = accessoryImageResources,
                    selectedIndex = uiState.selectedAccessoryIndex,
                    onChangeSelectedIndex = {
                        onEvent(CreateProfileUiEvent.SelectProfileImage(ProfileType.Accessory, it))
                    }
                )
                Spacer(modifier = Modifier.height(68.dp))
            }

            DefaultButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                isEnabled = uiState.profileName.isNotEmpty(),
                text = if (uiState.isUpdateProfile) {
                    "수정하기"
                } else {
                    "생성하기"
                },
                onClick = {
                    onEvent(
                        CreateProfileUiEvent.ClickConfirm(
                            userId = Settings.Secure.getString(
                                context.contentResolver,
                                Settings.Secure.ANDROID_ID
                            ),
                            faceImage = getBitmapFromResource(
                                context = context,
                                drawableId = faceImageResources[uiState.selectedFaceIndex]
                            ),
                            hairImage = getBitmapFromResource(
                                context = context,
                                drawableId = hairStyleImageResources[uiState.selectedHairStyleIndex]
                            ),
                            accessoryImage = getBitmapFromResource(
                                context = context,
                                drawableId = accessoryImageResources[uiState.selectedAccessoryIndex]
                            )
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    selectedFaceImageRes: Int,
    selectedHairImageRes: Int,
    selectedAccessoryImageRes: Int,
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                color = colorResource(id = R.color.light_gray_200),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = selectedFaceImageRes),
            contentDescription = "FaceImage"
        )
        if (selectedHairImageRes != R.drawable.none) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = selectedHairImageRes),
                contentDescription = "FaceImage"
            )
        }
        if (selectedAccessoryImageRes != R.drawable.none) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = selectedAccessoryImageRes),
                contentDescription = "FaceImage"
            )
        }
    }
}

@Composable
fun SelectStyleImage(
    modifier: Modifier = Modifier,
    styleTitle: String,
    styleImageResources: List<Int>,
    selectedIndex: Int = 0,
    onChangeSelectedIndex: (Int) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = styleTitle,
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(styleImageResources.size) { index ->
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(shape = MaterialTheme.shapes.small)
                        .background(
                            color = colorResource(id = R.color.light_gray),
                            shape = MaterialTheme.shapes.large
                        )
                        .clickable {
                            onChangeSelectedIndex(index)
                        }
                        .then(
                            if (selectedIndex == index) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = MaterialTheme.colors.secondary,
                                    shape = MaterialTheme.shapes.large
                                )
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(70.dp),
                        painter = painterResource(id = styleImageResources[index]),
                        contentDescription = "StyleImage"
                    )
                }
                if (index < styleImageResources.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfileImagePreview() {
    NeedTalkTheme {
        ProfileImage(
            selectedFaceImageRes = R.drawable.face2,
            selectedHairImageRes = R.drawable.hair10,
            selectedAccessoryImageRes = R.drawable.earring
        )
    }
}

@Preview
@Composable
private fun SetProfileNamePreview() {
    NeedTalkTheme {
        var name by remember { mutableStateOf("") }

        DefaultTextField(
            hint = "닉네임",
            inputData = name,
            onDataChange = {
                name = it
            }
        )
    }
}

@Preview
@Composable
private fun SelectStyleImagePreview() {
    NeedTalkTheme {
        SelectStyleImage(
            styleTitle = "표정",
            styleImageResources = listOf(),
            onChangeSelectedIndex = {}
        )
    }
}