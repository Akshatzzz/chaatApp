package com.plcoding.ktorandroidchat.presentation.username

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UserNAmeScreen(
    viewModel: UserNameViewModel,
    onNavigate: (String) -> Unit

) {
    LaunchedEffect(key1 = true) {
        viewModel.onJoinChat.collectLatest {userName ->
            onNavigate("chat_screen/$userName")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = viewModel.userNameText.value,
                onValueChange = viewModel::onUserNameChange,
                placeholder = { Text(text = "Enter username...") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.onJoinClick() }) {
                Text(text = "Join Chat")
            }
        }
    }
}