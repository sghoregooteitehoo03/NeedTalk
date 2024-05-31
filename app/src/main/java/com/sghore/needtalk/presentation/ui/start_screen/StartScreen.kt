package com.sghore.needtalk.presentation.ui.start_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.OnBoardingItem
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme

@Composable
fun StartScreen(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(56.dp))
            OnBoardingPager()
        }
        DefaultButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = "시작하기",
            onClick = onStartClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingPager(modifier: Modifier = Modifier) {
    val onBoardingItems = remember {
        listOf(
            OnBoardingItem(
                imageRes = R.drawable.onboard1,
                title = "환영합니다!",
                description = "대화가 필요해는 즐거운 대화를\n할 수 있도록 도와드리고 있습니다."
            ),
            OnBoardingItem(
                imageRes = R.drawable.onboard2,
                title = "다양한 대화주제들",
                description = "다양한 토픽의 대화주제들이 존재합니다.\n" +
                        "본인만의 대화모음집을 만들어보세요."
            ),
            OnBoardingItem(
                imageRes = R.drawable.onboard3,
                title = "대화에만 집중해요",
                description = "지정된 시간만이라도 휴대폰을\n" +
                        "내려놓고 대화에만 집중해 볼까요?"
            ),
            OnBoardingItem(
                imageRes = R.drawable.onboard4,
                title = "더욱 가까워지는 우리",
                description = "다른 이들과 대화를 통해\n" +
                        "친밀도를 쌓아 올려보세요."
            ),
            OnBoardingItem(
                imageRes = R.drawable.onboard5,
                title = "함께했던 대화를 기록해요",
                description = "기록된 대화 내용들에서 본인만의 \n" +
                        "하이라이트를 제작해 보세요."
            )
        )
    }
    val pagerState = rememberPagerState { onBoardingItems.size }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { index ->
            OnBoardingPagerItem(onBoardingItem = onBoardingItems[index])
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            repeat(pagerState.pageCount) { index ->
                val width = if (index == pagerState.currentPage) {
                    36.dp
                } else {
                    26.dp
                }
                val color = if (index == pagerState.currentPage) {
                    MaterialTheme.colors.secondary
                } else {
                    colorResource(id = R.color.light_gray_200)
                }

                Box(
                    modifier = Modifier
                        .width(width)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(color = color, shape = CircleShape)
                )

                if (index != pagerState.pageCount - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
fun OnBoardingPagerItem(
    modifier: Modifier = Modifier,
    imageSize: Dp = 280.dp,
    onBoardingItem: OnBoardingItem
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(imageSize),
            contentDescription = "OnBoarding Image",
            painter = painterResource(id = onBoardingItem.imageRes)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = onBoardingItem.title,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.onPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = onBoardingItem.description,
            style = MaterialTheme.typography.h5.copy(
                color = colorResource(id = R.color.gray),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview
@Composable
private fun StartScreenPreview() {
    NeedTalkTheme {
        StartScreen {

        }
    }
}