package com.sghore.needtalk.presentation.ui.permission_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.DefaultButton

@Composable
fun PermissionScreen(

) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        val (topbar, mainExplainText, subExplainText, permissionsLayout, button) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(topbar) {
                    top.linkTo(parent.top)
                }
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "권한 설정",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.primary)
            )
        }

        Text(
            modifier = Modifier.constrainAs(subExplainText) {
                top.linkTo(topbar.bottom)
                bottom.linkTo(permissionsLayout.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = "앱의 원할한 동작을 위해\n권한을 설정해주세요.",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Column(modifier = Modifier.constrainAs(permissionsLayout) {
            top.linkTo(topbar.bottom)
            bottom.linkTo(subExplainText.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {

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
            onClick = {}
        )
    }
}

@Composable
fun PermissionInfo(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    permissionName: String,
    permissionDescription: String
) {
    Row(modifier = modifier) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = iconPainter,
            contentDescription = "permissionIcon",
            tint = MaterialTheme.colors.primary
        )
    }
}