package com.example.pomodoroproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroproject.models.Session
import com.example.pomodoroproject.viewmodels.SessionRepository
import com.example.pomodoroproject.viewmodels.AuthRepository
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.pomodoroproject.R
import com.example.pomodoroproject.formatTimestamp
import com.example.pomodoroproject.ui.theme.PomodoroProjectTheme
import androidx.compose.foundation.layout.PaddingValues
import com.example.pomodoroproject.screens.BottomNavItem





@Composable
fun ViewSessionsScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sessionRepository = remember { SessionRepository(navHostController, context) }
    val authRepository = remember { AuthRepository(navHostController, context) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }


    LaunchedEffect(Unit) {
        if (!authRepository.isLoggedIn()) {
            authRepository.logOut()
        }

        val userId = authRepository.getUserId()
        if (userId != null) {
            sessionRepository.getUserSessions(userId) { result ->
                sessions = result
            }
        } else {

            if (!authRepository.isLoggedIn()) {
                authRepository.logOut()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (sessions.isEmpty() ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No sessions found.")
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 64.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(sessions) { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navHostController.navigate("session_info/${session.id}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${session.name}", style = MaterialTheme.typography.titleMedium)
                            Text("Date: ${formatTimestamp(session.date)}")
                            Text("Time: ${session.totalDuration} minutes")
                            Text("Completed Pomodoros: ${session.completedPomodoros}")
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Bottom Nav
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White)
                    .padding(bottom = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    iconRes = R.drawable.ic_dashboard,
                    label = "Dashboard",
                    selected = false,
                    onClick = { navHostController.navigate("dashboard") }
                )
                BottomNavItem(
                    iconRes = R.drawable.ic_selected_timer,
                    label = "View Session",
                    selected = true,
                    onClick = { }
                )
                BottomNavItem(
                    iconRes = R.drawable.ic_logout,
                    label = "Logout",
                    selected = false,
                    onClick = { showLogoutDialog = true }
                )
            }
        }


        // Logout Dialog
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
    }
}

@Preview(showBackground = true)
@Composable
fun ViewSessionsScreenPreview() {
    PomodoroProjectTheme {
        ViewSessionsScreen(navHostController = rememberNavController())
    }
}
