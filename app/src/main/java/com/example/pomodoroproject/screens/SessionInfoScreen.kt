package com.example.pomodoroproject.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.pomodoroproject.models.Session
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroproject.R
import com.example.pomodoroproject.ui.theme.PomodoroProjectTheme


@Composable
fun SessionInfoScreen(sessionId: String, navHostController: NavHostController) {
    // Replace with actual lookup from ViewModel or repository
    val session = remember {
        // Replace with real values if available
        Session(
            id = sessionId,
            name = "Deep Focus",
            userID = "sampleUserId", // replace with actual user ID
            pomodoroTime = 25,
            shortBreakTime = 5,
            longBreakTime = 15
        )
    }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = { navHostController.popBackStack() }) {
            Image(
                painter = painterResource(id = R.drawable.ic_return),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Session Details", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Name: ${session.name}")
        Text("Date: ${session.date}")
        Text("Time: ${session.totalDuration}")
        Text("Completed Pomodoros: ${session.completedPomodoros}")
    }
}

@Preview(showBackground = true)
@Composable
fun SessionInfoScreenPreview() {
    PomodoroProjectTheme {
        SessionInfoScreen(sessionId = "1", navHostController = rememberNavController())
    }
}
