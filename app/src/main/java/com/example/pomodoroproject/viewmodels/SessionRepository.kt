package com.example.pomodoroproject.viewmodels

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.navigation.NavHostController
import com.example.pomodoroproject.models.Session
import com.example.pomodoroproject.navigation.LOGIN_URL
import com.example.pomodoroproject.viewmodels.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SessionRepository(
    private val navHostController: NavHostController,
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val sessionRef = database.getReference("sessions")
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


}
