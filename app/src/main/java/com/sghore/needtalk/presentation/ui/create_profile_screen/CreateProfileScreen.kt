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
import androidx.compose.foundation.shape.CircleShape
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
    onEvent: (CreateProfileUiEvent) -> Unit
) {
    val context = LocalContext.current
    val faceImageResources = remember { getFaceStyleImageResources() }
    val hairStyleImageResources = remember { getHairStyleImageResources() }
    val accessoryImageResources = remember { getAccessoryStyleImageResources() }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "프로필 설정",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            val (mainLayout, button) = createRefs()

            Column(modifier = Modifier.constrainAs(mainLayout) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
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
            }

            DefaultButton(
                modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom)
                },
                isEnabled = uiState.profileName.isNotEmpty(),
                text = "확인",
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

private fun getFaceStyleImageResources() =
    listOf(
        R.drawable.face1,
        R.drawable.face2,
        R.drawable.face3,
        R.drawable.face4,
        R.drawable.face5,
        R.drawable.face6,
        R.drawable.face7,
    )

private fun getHairStyleImageResources() =
    listOf(
        R.drawable.none,
        R.drawable.hair1,
        R.drawable.hair2,
        R.drawable.hair3,
        R.drawable.hair4,
        R.drawable.hair5,
        R.drawable.hair6,
        R.drawable.hair7,
        R.drawable.hair8,
        R.drawable.hair9,
        R.drawable.hair10,
        R.drawable.hair11,
        R.drawable.hair12,
        R.drawable.hair13,
        R.drawable.hair14,
    )

private fun getAccessoryStyleImageResources() =
    listOf(
        R.drawable.none,
        R.drawable.earring,
        R.drawable.necklace,
        R.drawable.glasses,
        R.drawable.glasses2,
        R.drawable.sunglasses,
        R.drawable.ribbon,
        R.drawable.mask,
        R.drawable.smoke,
        R.drawable.earphone,
        R.drawable.headphone,
        R.drawable.hairband,
    )

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
            styleImageResources = getFaceStyleImageResources(),
            onChangeSelectedIndex = {}
        )
    }
}