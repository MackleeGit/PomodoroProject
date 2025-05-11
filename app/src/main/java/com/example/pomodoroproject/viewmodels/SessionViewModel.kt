package com.example.pomodoroproject.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroproject.models.Session
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    // Holds the current session state
    var session = mutableStateOf<Session?>(null)
        private set

    // Timer display state in MM:SS format
    var timerDisplay = mutableStateOf("00:00")
        private set

    // Application context for Toasts
    private val context = application.applicationContext

    // Initialize a new session
    fun initializeSession(sessionID: String, sessionName: String, userID: String, pomo: Int, short: Int, long: Int) {
        val newSession = Session(
            id = sessionID,
            name = sessionName,
            userID = userID,
            pomodoroTime = pomo,
            shortBreakTime = short,
            longBreakTime = long)

        newSession.activity = "POMODORO" // Set initial activity to Pomodoro
        newSession.timer = pomo * 60 // Timer in seconds
        newSession.completedPomodoros = 0
        newSession.shortBreaks = 0
        newSession.isPaused = false

        session.value = newSession
        updateTimerDisplay()
    }



    // Start the timer based on current activity
    fun startTimer() {
        if (session.value == null) {
            Toast.makeText(context, "No active session found.", Toast.LENGTH_SHORT).show()
            return
        }

        session.value?.isPaused = false

        val activity = session.value?.activity ?: ""

        if (activity == "POMODORO") {
            startCountdown(session.value!!.pomodoroTime)
        } else if (activity == "SHORT_BREAK") {
            startCountdown(session.value!!.shortBreakTime)
        } else if (activity == "LONG_BREAK") {
            startCountdown(session.value!!.longBreakTime)
        } else {
            Toast.makeText(context, "Unknown activity type.", Toast.LENGTH_SHORT).show()
        }
    }

    // Pause the timer
    fun pauseTimer() {
        if (session.value == null) {
            Toast.makeText(context, "Cannot pause. No session loaded.", Toast.LENGTH_SHORT).show()
            return
        }
        session.value?.isPaused = true
    }

    // Countdown logic with pause checking
    private fun startCountdown(durationMinutes: Int) {
        val totalSeconds = durationMinutes * 60
        session.value?.timer = totalSeconds

        viewModelScope.launch {
            while (session.value != null && session.value!!.timer > 0) {

                if (session.value?.isPaused == true) {
                    delay(1000L)
                    continue
                }

                delay(1000L)

                if (session.value != null) {
                    val currentTime = session.value!!.timer
                    session.value!!.timer = currentTime - 1
                    updateTimerDisplay()
                }
            }

            if (session.value != null && session.value!!.timer <= 0) {
                session.value!!.isPaused = true
                handleActivityComplete()
            }
        }
    }

    // Update timer display in MM:SS format
    private fun updateTimerDisplay() {
        if (session.value == null) {
            timerDisplay.value = "00:00"
            return
        }

        val totalSeconds = session.value!!.timer
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        timerDisplay.value = String.format("%02d:%02d", minutes, seconds)
    }

    // Handle what happens when a Pomodoro or break ends
    private fun handleActivityComplete() {
        if (session.value == null) {
            Toast.makeText(context, "Activity ended, but session data is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentActivity = session.value!!.activity

        if (currentActivity == "POMODORO") {
            val currentCount = session.value!!.completedPomodoros
            session.value!!.completedPomodoros = currentCount + 1
            Toast.makeText(context, "Pomodoro complete. Choose a break or end session.", Toast.LENGTH_LONG).show()

        } else if (currentActivity == "SHORT_BREAK"){
            session.value!!.shortBreaks = session.value!!.shortBreaks + 1
            Toast.makeText(context, "Break over. Ready for the next Pomodoro?", Toast.LENGTH_LONG).show()

        }else if (currentActivity == "LONG_BREAK") {
            Toast.makeText(context, "Break over. Ready for the next Pomodoro?", Toast.LENGTH_LONG).show()
            session.value!!.shortBreaks = session.value!!.shortBreaks + 1

        } else {
            Toast.makeText(context, "Unknown session state after completion.", Toast.LENGTH_SHORT).show()
        }

        // Prompting user should happen in the UI
    }
}
