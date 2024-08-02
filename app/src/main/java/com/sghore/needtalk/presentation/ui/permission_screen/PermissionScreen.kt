package com.sghore.needtalk.presentation.ui.permission_screen

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme

@Composable
fun PermissionScreen(onClickConfirm: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "권한 설정",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            val (mainExplainText, subExplainText, permissionsLayout, button) = createRefs()

            Text(
                modifier = Modifier.constrainAs(mainExplainText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(permissionsLayout.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                text = "앱의 원할한 동작을 위해\n권한을 설정해주세요.",
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Column(modifier = Modifier.constrainAs(permissionsLayout) {
                top.linkTo(parent.top)
                bottom.linkTo(subExplainText.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
                PermissionInfo(
                    iconPainter = painterResource(id = R.drawable.ic_alarm),
                    permissionName = "알림",
                    permissionDescription = "푸시알림 메시지 전송"
                )
                Spacer(modifier = Modifier.height(28.dp))
                PermissionInfo(
                    iconPainter = painterResource(id = R.drawable.ic_mic),
                    permissionName = "마이크",
                    permissionDescription = "대화내용 녹음"
                )
                Spacer(modifier = Modifier.height(28.dp))
                PermissionInfo(
                    iconPainter = painterResource(id = R.drawable.ic_nearby),
                    permissionName = "근처기기",
                    permissionDescription = "근처기기 탐색"
                )
                Spacer(modifier = Modifier.height(28.dp))
                PermissionInfo(
                    iconPainter = painterResource(id = R.drawable.ic_marker),
                    permissionName = "위치",
                    permissionDescription = "근처기기 탐색"
                )
            }

            Text(
                modifier = Modifier.constrainAs(subExplainText) {
                    start.linkTo(parent.start)
                    bottom.linkTo(button.top, margin = 14.dp)
                },
                text = "개인정보 수집에 사용되지 않습니다.",
                style = MaterialTheme.typography.body1.copy(color = colorResource(id = R.color.gray))
            )

            DefaultButton(
                modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom)
                },
                text = "확인",
                onClick = onClickConfirm
            )
        }
    }
}

@Composable
fun PermissionInfo(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    permissionName: String,
    permissionDescription: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = iconPainter,
            contentDescription = "permissionIcon",
            tint = MaterialTheme.colors.onPrimary
        )
        Spacer(modifier = Modifier.width(40.dp))
        Column {
            Text(
                text = permissionName,
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = permissionDescription,
                style = MaterialTheme.typography.h5.copy(color = colorResource(id = R.color.gray))
            )
        }
    }
}

@Preview
@Composable
private fun PermissionScreenPreview() {
    NeedTalkTheme {
        PermissionScreen {}
    }
}