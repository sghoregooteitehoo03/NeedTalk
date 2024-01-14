package com.sghore.needtalk.presentation.ui.create_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.presentation.ui.RoundedButton

@Composable
fun AddMusicDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    onClick: (url: String, title: String) -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        var youtubeURL by remember {
            mutableStateOf("")
        }
        var musicName by remember {
            mutableStateOf("")
        }


        Column(modifier) {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "유튜브에서 음악 추가",
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                value = youtubeURL,
                onValueChange = {
                    youtubeURL = it
                },
                placeholder = {
                    Text(text = "유튜브 주소")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.light_gray),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                value = musicName,
                onValueChange = {
                    musicName = it
                },
                placeholder = {
                    Text(text = "제목")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.light_gray),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box {
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "추가하기",
                    color = MaterialTheme.colors.secondary,
                    textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary),
                    paddingValues = PaddingValues(14.dp),
                    enable = youtubeURL.isNotEmpty() && musicName.isNotEmpty(),
                    onClick = {
                        if (!isLoading) {
                            onClick(youtubeURL, musicName)
                        }
                    }
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 7.dp, end = 14.dp, bottom = 7.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun RemoveMusicDialog(
    modifier: Modifier = Modifier,
    musicEntity: MusicEntity,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    onClick: (id: String) -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp)),
                painter = rememberAsyncImagePainter(musicEntity.thumbnailImage),
                contentDescription = musicEntity.thumbnailImage,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "(${musicEntity.title})\n삭제하시겠습니까?",
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box {
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "삭제하기",
                    color = MaterialTheme.colors.secondary,
                    textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary),
                    paddingValues = PaddingValues(14.dp),
                    onClick = {
                        if (!isLoading) {
                            onClick(musicEntity.id)
                        }
                    }
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 7.dp, end = 14.dp, bottom = 7.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            RoundedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.light_gray),
                        shape = MaterialTheme.shapes.large
                    ),
                text = "취소",
                color = Color.Transparent,
                textStyle = MaterialTheme.typography.body1.copy(color = colorResource(id = R.color.gray)),
                paddingValues = PaddingValues(14.dp),
                onClick = {
                    onDismiss()
                }
            )
        }
    }
}