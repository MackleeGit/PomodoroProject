package com.example.pomodoroproject.viewmodels

import android.app.ProgressDialog
import android.content.Context
import androidx.navigation.NavHostController
import com.example.pomodoroproject.models.Session
import com.example.pomodoroproject.navigation.LOGIN_URL
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class SessionRepository(
    navHostController: NavHostController,
    private val context: Context
) {
    private val authRepository = AuthRepository(navHostController, context)
    private val progress = ProgressDialog(context).apply {
        setTitle("Loading")
        setMessage("Please wait...")
    }

    init {
        if (!authRepository.isLoggedIn()) {
            navHostController.navigate(LOGIN_URL)
        }
    }




    fun SaveSession(session: Session?) {
        progress.show()

        if (session == null || session.userID.isBlank()) {

            progress.dismiss()
            Toast.makeText(context, "Invalid session or missing user ID", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Sessions").child(session.userID) // store sessions *under* the userID

        // Clean session data to save
        val sessionData = mapOf(
            "id" to session.id,
            "name" to session.name,
            "date" to session.date,
            "userID" to session.userID,
            "pomodoroTime" to session.pomodoroTime,
            "shortBreakTime" to session.shortBreakTime,
            "longBreakTime" to session.longBreakTime,
            "completedPomodoros" to session.completedPomodoros,
            "shortBreaks" to session.shortBreaks,
            "longBreaks" to session.longBreaks,
            "totalDuration" to session.totalDuration,
            "workingTime" to session.workingtime,
            "activity" to session.activity
        )

        ref.child(session.id).setValue(sessionData)
            .addOnSuccessListener {
                progress.dismiss()
                Toast.makeText(context, "Session saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                progress.dismiss()

                Toast.makeText(context, "Failed to save session: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun getUserSessions(userId: String, onResult: (List<Session>) -> Unit) {
        progress.show()
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Sessions")
            .child(userId)

        dbRef.get()
            .addOnSuccessListener {

                snapshot ->
                val sessions = mutableListOf<Session>()
                for (child in snapshot.children) {
                    val session = child.getValue(Session::class.java)
                    if (session != null) {
                        sessions.add(session)
                    }
                }
                onResult(sessions)
                progress.dismiss()
            }
            .addOnFailureListener {
                onResult(emptyList()) // handle errors gracefully
                progress.dismiss()
            }
    }
}


