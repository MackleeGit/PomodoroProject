package com.example.pomodoroproject.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroproject.models.Session
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    // Holds the current session state
    var session = mutableStateOf<Session?>(null)
        private set

    var completedActivity = mutableStateOf<String?>(null)
        private set

    // Timer display state in MM:SS format
    var timerDisplay = mutableStateOf("00:00")
        private set

    private var countdownJob: Job? = null

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
        newSession.isSkipped = false



        session.value = newSession
        updateTimerDisplay()
    }



    // Start the timer based on current activity
    fun startTimer() {
        if (session.value == null) {
            Toast.makeText(getApplication<Application>(), "No active session found.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(getApplication<Application>(), "Unknown activity type.", Toast.LENGTH_SHORT).show()
        }
    }

    fun pauseCountdown() {
        session.value?.isPaused = true
        session.value = session.value // trigger recomposition
    }

    fun resumeCountdown() {
        session.value?.isPaused = false
        session.value = session.value
    }




    fun skipActivity() {
        countdownJob?.cancel()
        session.value?.isCountdown = false
        session.value!!.isPaused = true
        session.value?.isSkipped = true
        session.value?.timer = 0
        updateTimerDisplay()
        handleActivityComplete()

    }



    // Countdown logic with pause checking
    private fun startCountdown(durationMinutes: Int) {
        countdownJob = viewModelScope.launch {
            session.value?.isCountdown = true
            val totalSeconds = durationMinutes * 60
            session.value?.isPaused = false
            session.value?.timer = totalSeconds

            while (session.value != null && session.value!!.timer > 0) {
                if (session.value?.isCountdown == false) break

                if (session.value?.isPaused == true) {
                    delay(1000L)
                    continue
                }

                delay(1000L)

                session.value?.let {
                    it.timer = it.timer - 1
                    updateTimerDisplay()
                }
            }

            if (session.value != null && session.value!!.timer <= 0) {
                session.value!!.isPaused = true
                session.value?.isCountdown = false
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
        timerDisplay.value = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }





    fun clearCompletedActivity() {
        completedActivity.value = null
    }




    // Handle what happens when a Pomodoro or break ends
    private fun handleActivityComplete() {
        val currentSession = session.value ?: run {
            Toast.makeText(getApplication<Application>(), "Activity ended, but session data is missing.", Toast.LENGTH_SHORT).show()
            return
        }


        val wasSkipped = currentSession.isSkipped
        session.value?.timer = 0
        updateTimerDisplay()

        val currentActivity = session.value!!.activity

        if (currentActivity == "POMODORO"){
            if (!wasSkipped) {
                session.value!!.completedPomodoros += 1
                Toast.makeText(getApplication<Application>(), "Pomodoro Complete.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication<Application>(), "Pomodoro skipped. It will not be counted.", Toast.LENGTH_SHORT).show()
            }
            session.value!!.isSkipped = false // Reset immediately after check
            completedActivity.value = "POMODORO"

        }else if (currentActivity == "SHORT_BREAK"){
            if (!wasSkipped) {
                session.value!!.shortBreaks += 1
            } else {
                Toast.makeText(getApplication<Application>(), "Break session skipped. It will not be counted.", Toast.LENGTH_SHORT).show()
            }
            session.value!!.isSkipped = false
            completedActivity.value = "SHORT_BREAK"

        }else{
            if (!wasSkipped) {
                session.value!!.longBreaks += 1
            } else {
                Toast.makeText(getApplication<Application>(), "Break session skipped. It will not be counted.", Toast.LENGTH_SHORT).show()
            }
            session.value!!.isSkipped = false
            completedActivity.value = "LONG_BREAK"
        }
    }


    fun cycleActivity() {
            if(session.value?.isCountdown == false){

                session.value?.let {
                    it.activity = when (it.activity) {
                        "POMODORO" -> "SHORT_BREAK"
                        "SHORT_BREAK" -> "LONG_BREAK"
                        else -> "POMODORO"
                    }
                    session.value = it // trigger recomposition
                    updateTimerDisplay()
                }


            }else{
                Toast.makeText(getApplication<Application>(), "Cannot change activity during countdown.", Toast.LENGTH_SHORT).show()
            }

    }
}


