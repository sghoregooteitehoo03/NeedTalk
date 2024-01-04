package com.sghore.needtalk.presentation.ui.home_screen

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
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.NameTag
import com.sghore.needtalk.presentation.ui.TopBar
import com.sghore.needtalk.presentation.ui.theme.Purple80

@Composable
fun HomeScreen(

) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(start = 14.dp, end = 14.dp),
                content = { modifier ->
                    NameTag(
                        modifier = modifier,
                        name = "테스트에요",
                        color = Purple80,
                        interval = 6.dp,
                        colorSize = 16.dp,
                        textStyle = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        ),
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