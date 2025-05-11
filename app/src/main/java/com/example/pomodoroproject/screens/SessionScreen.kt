package com.example.pomodoroproject.screens

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pomodoroproject.viewmodels.SessionViewModel

@Composable
fun SessionScreen(
    navHostController: NavHostController,
    sessionId: String,
    sessionName: String,
    userId: String,
    pomo: Int,
    short: Int,
    long: Int,
    viewModel: SessionViewModel
) {
    val context = LocalContext.current
    val session = viewModel.session.value
    val timerDisplay = viewModel.timerDisplay.value
    var showSessionEndDialog by remember { mutableStateOf(false) }

    // Initialize the session when the screen is first loaded
    LaunchedEffect(key1 = sessionId) {
        viewModel.initializeSession(sessionId, sessionName, userId, pomo, short, long)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        // Session name and current activity (e.g., Pomodoro, Short Break)
        Text("Session: ${session?.name ?: "No Session"}", style = MaterialTheme.typography.headlineSmall)
        Text("Activity: ${session?.activity ?: "No Activity"}", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Timer display in MM:SS format
        Text(text = timerDisplay, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        // Start / Pause / Stop Button
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { viewModel.startTimer() }) {
                Text("Start")
            }

            Button(onClick = { viewModel.pauseTimer() }) {
                Text("Pause")
            }

            Button(onClick = {
                showSessionEndDialog = true
            }) {
                Text("End Session")
            }
        }

        // Session End Confirmation Dialog
        if (showSessionEndDialog) {
            AlertDialog(
                onDismissRequest = { showSessionEndDialog = false },
                title = { Text("End Session") },
                text = { Text("Are you sure you want to end the session?") },
                confirmButton = {
                    TextButton(onClick = {
                        // Logic to end the session and navigate back to the dashboard
                        showSessionEndDialog = false
                        navHostController.navigate("dashboard")
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSessionEndDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
