package com.hobotech.facesenseai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hobotech.facesenseai.greeting.greetingViewModel
import org.jetbrains.compose.resources.painterResource

import facesenseai.composeapp.generated.resources.Res
import facesenseai.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = greetingViewModel()
        val state by viewModel.state.collectAsState(initial = viewModel.state.value)
        var showContent by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                showContent = !showContent
                if (showContent) viewModel.loadGreeting()
            }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when {
                        state.isLoading -> CircularProgressIndicator()
                        state.error != null -> Text(state.error ?: "")
                        else -> {
                            Image(painterResource(Res.drawable.compose_multiplatform), null)
                            Text("Compose: ${state.message}")
                        }
                    }
                }
            }
        }
    }
}