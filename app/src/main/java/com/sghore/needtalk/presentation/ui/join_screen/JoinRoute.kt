package com.sghore.needtalk.presentation.ui.join_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun JoinRoute(
    viewModel: JoinViewModel = hiltViewModel()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val text = if (viewModel.isFound) {
            "Client: Found it!!"
        } else {
            "Client: Founding..."
        }
        Text(text = text)
    }
}