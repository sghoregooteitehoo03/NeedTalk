package com.sghore.needtalk.presentation.ui.home_screen.talk_history_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.needtalk.R

@Composable
fun TalkHistoryScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_history),
                contentDescription = "EmptyHistory",
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "대화기록이 존재하지 않습니다.",
                style = MaterialTheme.typography.h5.copy(
                    color = colorResource(id = R.color.gray),
                    fontSize = 18.sp
                )
            )
        }
    }
}