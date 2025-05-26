package com.example.pomodoroproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.delay
import com.example.pomodoroproject.models.Session
import com.example.pomodoroproject.viewmodels.SessionRepository
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroproject.R
import com.example.pomodoroproject.ui.theme.PomodoroProjectTheme
import com.example.pomodoroproject.viewmodels.AuthRepository
import com.example.pomodoroproject.getFormattedDate
import com.example.pomodoroproject.getFormattedTime
import com.example.pomodoroproject.screens.BottomNavItem



@Composable
fun DashboardScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val authRepository = AuthRepository(navHostController, context)
    val sessionRepository = remember { SessionRepository(navHostController, context) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSessionInitDialog by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("...") }
    var userID by remember { mutableStateOf("") }
    var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }

    val currentTime = remember { mutableStateOf(getFormattedTime()) }
    val currentDate = remember { getFormattedDate() }

    // Clock updater
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime.value = getFormattedTime()
        }
    }

    LaunchedEffect(Unit) {
        if (!authRepository.isLoggedIn()) {
            authRepository.logOut()
        }

        authRepository.fetchUsernameFromDatabase {
            username = it
        }

        userID = authRepository.getUserId()
        sessionRepository.getUserSessions(userID) { result ->
            sessions = result
        }
    }

    val longestSession = sessions.maxByOrNull { it.totalDuration }
    val sessionCount = sessions.size

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Row: Welcome + Date + Time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Welcome, $username!", style = MaterialTheme.typography.titleMedium)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(currentDate, style = MaterialTheme.typography.bodyMedium)
                Text(currentTime.value, style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(painter = painterResource(id = R.drawable.ic_logout), contentDescription = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats section
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Dashboard Stats", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Sessions Completed: $sessionCount")

            if (longestSession != null) {
                val minutes = longestSession.totalDuration

                Text("Longest Session: ${longestSession.name} - ${minutes} minutes")
            } else {
                Text("Longest Session: Not found")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start Session Button
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = { showSessionInitDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start New Session")
            }
        }
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log out") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        authRepository.logOut()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Nav
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                iconRes = R.drawable.ic_selected_dashboard,
                label = "Dashboard",
                selected = true,
                onClick = {  }
            )

            BottomNavItem(
                iconRes = R.drawable.ic_timer,
                label = "View Sessions",
                selected = false,
                onClick = { navHostController.navigate("view_sessions") }
            )
            BottomNavItem(
                iconRes = R.drawable.ic_logout,
                label = "Logout",
                selected = false,
                onClick = { showLogoutDialog = true }
            )
        }
    }


    if (showSessionInitDialog) {
        var sessionName by remember { mutableStateOf("") }
        var pomoTimeInput by remember { mutableStateOf("25") }
        var shortBreakInput by remember { mutableStateOf("5") }
        var longBreakInput by remember { mutableStateOf("15") }

        AlertDialog(
            onDismissRequest = { showSessionInitDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val pomo = pomoTimeInput.toIntOrNull() ?: 25
                        val short = shortBreakInput.toIntOrNull() ?: 5
                        val long = longBreakInput.toIntOrNull() ?: 15
                        if (sessionName.isNotBlank()) {
                            val sessionId = System.currentTimeMillis().toString()
                            val userId = userID

                            navHostController.navigate(
                                "start_session/${sessionId}/${sessionName}/${userId}/${pomo}/${short}/${long}"
                            )
                            showSessionInitDialog = false
                        }
                    }
                ) {
                    Text("Start Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSessionInitDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("New Session Setup") },
            text = {
                Column {
                    OutlinedTextField(
                        value = sessionName,
                        onValueChange = { sessionName = it },
                        label = { Text("Session Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pomoTimeInput,
                        onValueChange = { if (it.all(Char::isDigit)) pomoTimeInput = it },
                        label = { Text("Pomodoro Length (min)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shortBreakInput,
                        onValueChange = { if (it.all(Char::isDigit)) shortBreakInput = it },
                        label = { Text("Short Break Length (min)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = longBreakInput,
                        onValueChange = { if (it.all(Char::isDigit)) longBreakInput = it },
                        label = { Text("Long Break Length (min)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    // Keep your existing session setup AlertDialog here...
}



@Preview(showBackground = true)
@Composable
fun DashboardScreenPreviewTest() {
    PomodoroProjectTheme {
        DashboardScreen(navHostController = rememberNavController())
    }
}



