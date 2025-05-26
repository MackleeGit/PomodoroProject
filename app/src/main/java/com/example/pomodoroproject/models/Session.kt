package com.example.pomodoroproject.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class Session {
    var id: String = ""
    var name: String = ""
    var date: Long = System.currentTimeMillis()
    var userID: String = ""

    var completedPomodoros: Int = 0
    var shortBreaks: Int = 0
    var longBreaks: Int = 0

    var pomodoroTime: Int = 0
    var shortBreakTime: Int = 0
    var longBreakTime: Int = 0

    val totalDuration:Int
        get() = (completedPomodoros * pomodoroTime) +
                (shortBreaks * shortBreakTime) +
                (longBreaks * longBreakTime)

    var activity by  mutableStateOf("")
    var timer: Int = 0

    val workingtime: Int
        get() = completedPomodoros * pomodoroTime



    var isPaused: Boolean = true
    var isCountdown: Boolean = false
    var sessionActive: Boolean = false
    var isSkipped: Boolean = false

    constructor()

    constructor(id: String, name: String, userID: String, pomodoroTime: Int,shortBreakTime: Int, longBreakTime: Int ) {
        this.id = id
        this.name = name
        this.date = System.currentTimeMillis()
        this.userID = userID

        this.pomodoroTime = pomodoroTime
        this.longBreakTime = longBreakTime
        this.shortBreakTime = shortBreakTime

        this.activity = "POMODORO"
        this.timer = pomodoroTime * 60

    }




    fun startTimer() {
        isPaused = false
        sessionActive = true
        when (activity) {
            "POMODORO" -> pomodoro()
            "SHORT_BREAK" -> shortBreak()
            "LONG_BREAK" -> longBreak()
        }
    }


    private fun pomodoro() {
        timer = pomodoroTime * 60
        countdown {
            completedPomodoros++
            isPaused = true
            // prompt for next step (UI should trigger this)
        }
    }

    private fun shortBreak() {
        timer = shortBreakTime * 60
        countdown {
            shortBreaks++
            isPaused = true
            // prompt for next Pomodoro (UI should trigger this)
        }
    }

    private fun longBreak() {
        timer = longBreakTime * 60
        countdown {
            longBreaks++
            isPaused = true
            // prompt for next Pomodoro (UI should trigger this)
        }
    }

    private fun countdown(onFinish: () -> Unit) {
        Thread {
            while (timer > 0) {
                if (!isPaused) {
                    Thread.sleep(1000)
                    timer--
                }
            }
            if (timer == 0) {
                isPaused = true
                onFinish()
            }
        }.start()
    }

    fun endSession() {
        isPaused = true
        sessionActive = false
        // logic to store session, return to dashboard, etc.
    }
}












