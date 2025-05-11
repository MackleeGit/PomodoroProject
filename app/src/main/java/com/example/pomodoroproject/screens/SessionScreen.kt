package com.example.pomodoroproject.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val completedActivity = viewModel.completedActivity.value
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showSessionEndDialog by remember { mutableStateOf(false)}
    var showSkipDialog by remember { mutableStateOf(false) }


    LaunchedEffect(completedActivity) {
        if (completedActivity != null) {
            showCompletionDialog = true
        }
    }

    // Initialize the session when the screen is first loaded
    LaunchedEffect(key1 = sessionId) {
        viewModel.initializeSession(sessionId, sessionName, userId, pomo, short, long)
    }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        // Session name and current activity (e.g., Pomodoro, Short Break)
        Text("Session: ${session?.name ?: "No Session"}", style = MaterialTheme.typography.headlineSmall)
        Row(modifier = Modifier.fillMaxWidth()){
            Text("Activity:", style = MaterialTheme.typography.titleMedium)
            if (session?.activity == "POMODORO") {
                TextButton(onClick = { viewModel.cycleActivity() }) {
                    Text("POMODORO")
                }
            } else if (session?.activity == "SHORT_BREAK") {
                TextButton(onClick = { viewModel.cycleActivity() }) {
                    Text("SHORT_BREAK")
                }
            }else{
                TextButton(onClick = { viewModel.cycleActivity() }) {
                Text("LONG_BREAK")
            }

            }

        }






        Spacer(modifier = Modifier.height(32.dp))

        // Timer display in MM:SS format
        Text(text = timerDisplay, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        // Start / Pause / Stop Button
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (session?.isCountdown == true) {
                Button(onClick = { showSkipDialog = true }) {
                    Text("Skip")
                }
            } else {
                Button(onClick = { viewModel.startTimer() }) {
                    Text("Start")
                }
            }

            Button(onClick = { viewModel.pauseButton() }) {
                Text("Pause")
            }

            Button(onClick = {
                showSessionEndDialog = true
            }) {
                Text("End Session")
            }
        }

        val message = when (completedActivity) {
            "POMODORO" -> "Pomodoro complete. Choose your next break."
            "SHORT_BREAK", "LONG_BREAK" -> "Break over. Ready for the next Pomodoro?"
            else -> "Activity complete. What would you like to do next?"
        }


        if (showCompletionDialog){
        AlertDialog(
            onDismissRequest = {
                showCompletionDialog = false
                viewModel.clearCompletedActivity()
            },
            title = { Text("Activity Complete") },
            text = { Text(message) },
            confirmButton = {
                when (completedActivity) {
                    "POMODORO" -> {
                        Column {
                            TextButton(onClick = {
                                showCompletionDialog = false
                                session?.activity = "SHORT_BREAK"
                                viewModel.clearCompletedActivity()
                                viewModel.startTimer() // Start next segment
                            }) {
                                Text("Begin Short Break")
                            }

                            TextButton(onClick = {
                                showCompletionDialog = false
                                session?.activity = "LONG_BREAK"
                                viewModel.clearCompletedActivity()
                                viewModel.startTimer() // Start next segment
                            }) {
                                Text("Begin Long Break")
                            }
                        }
                    }
                    else -> {
                        TextButton(onClick = {
                            showCompletionDialog = false
                            session?.activity = "POMODORO"
                            viewModel.clearCompletedActivity()
                            viewModel.startTimer() // Start next segment
                        }) {
                            Text("Begin Next Pomodoro")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCompletionDialog = false
                    viewModel.clearCompletedActivity()
                    viewModel.cycleActivity()
                }) {
                    Text("Not now")
                }
            }
        )}

        if (showSkipDialog) {
            AlertDialog(
                onDismissRequest = { showSkipDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.skipActivity() // sets isCountdown = false
                        showSkipDialog = false
                    }) {
                        Text("Yes, skip")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSkipDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Skip current activity?") },
                text = { Text("Are you sure you want to skip this Pomodoro or break?") }
            )
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
