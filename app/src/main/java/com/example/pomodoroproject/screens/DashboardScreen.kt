package com.example.pomodoroproject.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroproject.R
import com.example.pomodoroproject.ui.theme.PomodoroProjectTheme
import com.example.pomodoroproject.viewmodels.AuthRepository



@Composable
fun DashboardScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val authRepository = AuthRepository(navHostController, context)
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSessionInitDialog by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        // Top Bar with username and logout button
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Welcome, username", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(painter = painterResource(id = R.drawable.ic_logout), contentDescription = "Logout")
            }
        }

        // Logout confirmation dialog
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

        // Session info section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Longest Session:", style = MaterialTheme.typography.titleMedium)
            Text("Pomodoro Sprint - 1h 20m")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Sessions Done:", style = MaterialTheme.typography.titleMedium)
            Text("12")
        }

        // Action buttons for View Sessions and Start New Session
        Column(
            modifier = Modifier
                .fillMaxWidth()

                .padding(16.dp)
        ) {
            Button(
                onClick = { navHostController.navigate("view_sessions") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Sessions")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    //if (authRepository.isLoggedIn()) {
                        showSessionInitDialog = true
                    //} else {
                      //  Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
                    //}
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start New Session")
            }
        }

        var sessionName by remember { mutableStateOf("") }
        var pomoTimeInput by remember { mutableStateOf("25") }
        var shortBreakInput by remember { mutableStateOf("5") }
        var longBreakInput by remember { mutableStateOf("15") }

        if (showSessionInitDialog) {
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
                                val userId = 1 // Replace with auth logic if needed
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




        // Bottom Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                iconRes = R.drawable.ic_selected_dashboard,
                label = "Dashboard",
                onClick = { navHostController.navigate("dashboard") }
            )
            BottomNavItem(
                iconRes = R.drawable.ic_timer,
                label = "Session",
                onClick = { navHostController.navigate("start_session") }
            )
            BottomNavItem(
                iconRes = R.drawable.ic_logout,
                label = "Logout",
                onClick = { showLogoutDialog = true }
            )
        }
    }
}

@Composable
fun BottomNavItem(iconRes: Int, label: String, onClick: () -> Unit) {

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreviewTest() {
    PomodoroProjectTheme {
        DashboardScreen(navHostController = rememberNavController())
    }
}



