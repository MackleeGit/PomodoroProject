package com.example.pomodoroproject.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pomodoroproject.navigation.DASHBOARD_URL
import com.example.pomodoroproject.viewmodels.SessionViewModel
import com.example.pomodoroproject.viewmodels.SessionRepository
import androidx.compose.ui.text.font.FontWeight
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
    val sessionRepository = SessionRepository(navHostController, context)
    val completedActivity = viewModel.completedActivity.value
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showSessionEndDialog by remember { mutableStateOf(false)}
    var showSavePromptDialog by remember { mutableStateOf(false)}
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


    Column( modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .padding(top = 24.dp)
        .wrapContentSize(align = Alignment.TopCenter)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = session?.name ?: "No Session",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .wrapContentHeight(),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Activity: ", style = MaterialTheme.typography.titleMedium)

            TextButton(onClick = { viewModel.cycleActivity() }) {
                Text(session?.activity ?: "N/A", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        // Live count row for Pomos and Breaks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pomodoros: ${session?.completedPomodoros ?: 0}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Short Breaks: ${session?.shortBreaks ?: 0}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Long Breaks: ${session?.longBreaks ?: 0}",
                style = MaterialTheme.typography.titleSmall
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timerDisplay,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start / Pause / Stop Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val buttonSize = 100.dp

            Button(
                onClick = {
                    if (session?.isCountdown == true) showSkipDialog = true
                    else viewModel.startTimer()
                },
                modifier = Modifier
                    .size(buttonSize)
            ) {
                Text(if (session?.isCountdown == true) "Skip" else "Start", textAlign = TextAlign.Center)
            }

            Button(
                onClick = { if(session!!.isPaused) viewModel.resumeCountdown() else viewModel.pauseCountdown() },
                modifier = Modifier
                    .size(buttonSize)
            ) {
                Text(if (session?.isPaused == true) "Resume" else "Pause", textAlign = TextAlign.Center)
            }

            Button(
                onClick = { showSessionEndDialog = true },
                modifier = Modifier
                    .size(buttonSize)
            ) {
                Text("End", textAlign = TextAlign.Center)
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
            viewModel.pauseCountdown()
            AlertDialog(
                onDismissRequest = { showSkipDialog = false
                 viewModel.resumeCountdown()
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.skipActivity() // sets isCountdown = false
                        showSkipDialog = false
                    }) {
                        Text("Yes, skip")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showSkipDialog = false
                        viewModel.resumeCountdown() }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Skip current activity?") },
                text = { Text("Are you sure you want to skip this Pomodoro or break?") }
            )
        }



        // Session End Confirmation Dialog
        if (showSessionEndDialog) {
            viewModel.pauseCountdown()
            AlertDialog(
                onDismissRequest = {
                    showSessionEndDialog = false
                    viewModel.resumeCountdown()
                },

                title = { Text("End Session") },
                text = { Text("Are you sure you want to end the session?") },
                confirmButton = {
                    TextButton(onClick = {
                        showSessionEndDialog = false
                        showSavePromptDialog = true

                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showSessionEndDialog = false
                        viewModel.resumeCountdown()
                    }) {
                        Text("No")
                    }
                }
            )
        }

        // Session Save Confirmation Dialog
        if (showSavePromptDialog) {
            val completedpomos = (session?.completedPomodoros?: 0)
            if (completedpomos < 1){
                AlertDialog(
                    onDismissRequest = {
                        showSavePromptDialog = false
                        viewModel.resumeCountdown()
                    },

                    title = { Text("Save Session?") },
                    text = { Text("You have not completed at least one pomodoro. This session will not be saved. (Click outside the dialog box to cancel session termination.)") },
                    confirmButton = {
                        TextButton(onClick = {
                            showSavePromptDialog = false
                            navHostController.navigate(DASHBOARD_URL)


                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showSavePromptDialog = false
                                viewModel.resumeCountdown()
                            }) {
                            Text("No, proceed with session")
                        }
                    }
                )
            }else{
                AlertDialog(
                onDismissRequest = {
                    showSavePromptDialog = false
                    viewModel.resumeCountdown()
                 },

                    title = { Text("Save Session?") },
                text = { Text("Do you wish to save the current session before terminating? (Click outside the dialog box to cancel session termination.)") },
                confirmButton = {
                    TextButton(onClick = {

                        showSavePromptDialog = false
                        session?.let {
                            sessionRepository.SaveSession(it)
                            navHostController.navigate(DASHBOARD_URL)
                        }

                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSavePromptDialog = false
                            navHostController.navigate(DASHBOARD_URL)
                        }) {
                        Text("No, quit without saving ")
                    }
                }
            )

            }

        }





    }
}
