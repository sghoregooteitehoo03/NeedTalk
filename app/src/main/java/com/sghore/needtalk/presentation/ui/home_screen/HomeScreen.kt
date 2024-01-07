package com.sghore.needtalk.presentation.ui.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.UserEntity
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.presentation.ui.NameTag
import com.sghore.needtalk.presentation.ui.RoundedButton
import com.sghore.needtalk.presentation.ui.TopBar
import com.sghore.needtalk.presentation.ui.theme.Blue
import com.sghore.needtalk.presentation.ui.theme.Green
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Orange
import com.sghore.needtalk.presentation.ui.theme.Orange50
import com.sghore.needtalk.presentation.ui.theme.Purple
import java.text.SimpleDateFormat
import java.util.Locale

// TODO:
//  . 액션 기능

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    pagingItems: LazyPagingItems<TalkHistory>?,
    onEvent: (HomeUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            val user = uiState.user ?: UserEntity("", "", 0)

            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(start = 14.dp, end = 14.dp),
                content = { modifier ->

                    NameTag(
                        modifier = modifier.clickable {
                            onEvent(HomeUiEvent.ClickNameTag)
                        },
                        name = user.name,
                        color = Color(user.color),
                        interval = 6.dp,
                        colorSize = 16.dp,
                        textStyle = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                        isBorder = true
                    )
                },
                actions = { modifier ->
                    Icon(
                        modifier = modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_graph),
                        contentDescription = "Graph",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            )
            pagingItems?.let { talkHistory ->
                LazyColumn {
                    items(talkHistory.itemCount) { index ->
                        TalkHistoryItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            owner = user,
                            talkHistory = talkHistory[index]
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp),
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "시작하기",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary)
                    )
                }
            },
            onClick = { /*TODO*/ }
        )
    }
}

@Composable
fun TalkHistoryItem(
    modifier: Modifier = Modifier,
    owner: UserEntity,
    talkHistory: TalkHistory?
) {
    Column {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = SimpleDateFormat(
                    "yyyy년 M월 d일 (E)",
                    Locale.KOREA
                ).format(talkHistory?.createTimeStamp ?: 0L),
                style = MaterialTheme.typography.subtitle1.copy(color = colorResource(id = R.color.gray))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = SimpleDateFormat(
                    "H시간 m분",
                    Locale.KOREA
                ).format(talkHistory?.talkTime ?: 0L),
                style = MaterialTheme.typography.h3
            )
            Text(
                text = "대화에 집중 한 시간",
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary.copy(
                        alpha = 0.6f
                    )
                )
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row {
                repeat(talkHistory?.users?.size ?: 0) { index ->
                    val user = talkHistory?.users?.get(index)

                    Spacer(modifier = Modifier.width(8.dp))
                    NameTag(
                        name = user?.name ?: "",
                        color = Color(user?.color ?: 0),
                        interval = 4.dp,
                        colorSize = 10.dp,
                        textStyle = if (owner.userId == (user?.userId ?: "")) {
                            MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                        } else {
                            MaterialTheme.typography.body1
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Divider(color = colorResource(id = R.color.light_gray), thickness = 2.dp)
    }
}

@Composable
fun SetName(
    modifier: Modifier,
    userName: String,
    onCloseClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    var name by remember { mutableStateOf(userName) }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "닉네임 설정",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { onCloseClick() }
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
            value = name,
            onValueChange = {
                if (it.length <= 6) {
                    name = it
                }
            },
            placeholder = {
                Text(text = "닉네임 입력")
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
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "닉네임은 최대 6글자까지 설정 가능합니다.",
            style = MaterialTheme.typography.subtitle2.copy(color = colorResource(id = R.color.gray))
        )
        Spacer(modifier = Modifier.height(24.dp))
        RoundedButton(
            modifier = Modifier.fillMaxWidth(),
            text = "수정하기",
            color = Orange50,
            textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSecondary),
            paddingValues = PaddingValues(14.dp),
            enable = name.isNotEmpty() && name != userName,
            onClick = { onEditClick(name) }
        )
    }
}

@Preview
@Composable
fun TalkHistoryItemPreview() {
    val owner = UserEntity("asdf", "김수한무", color = Purple.toArgb())
    val testData = TalkHistory(
        talkTime = (3600000L).minus(32400000),
        users = listOf(
            owner,
            UserEntity("bdsc", "거북이", color = Blue.toArgb()),
            UserEntity("hsef", "두루미", color = Green.toArgb())
        ),
        createTimeStamp = System.currentTimeMillis()
    )
    NeedTalkTheme {
        TalkHistoryItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            owner = owner,
            talkHistory = testData
        )
    }
}

@Preview
@Composable
fun SetNamePreview() {
    NeedTalkTheme {
        SetName(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            userName = "닉네임",
            onCloseClick = {},
            onEditClick = {}
        )
    }
}