package com.example.pomodoroproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroproject.models.Session
import com.example.pomodoroproject.ui.theme.PomodoroProjectTheme


@Composable
fun ViewSessionsScreen(navHostController: NavHostController) {
    // Replace with actual data from ViewModel or repository
    val sessions = listOf(
        Session("1", "Sprint A", "user123", 25, 5, 15),
        Session("2", "Deep Work", "user123", 50, 10, 30)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        sessions.forEach { session ->
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
                    Text("Date: ${session.date}")
                    Text("Time: ${session.totalDuration} minutes")
                    Text("Completed Pomodoros: ${session.completedPomodoros}")
                }
            }
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
