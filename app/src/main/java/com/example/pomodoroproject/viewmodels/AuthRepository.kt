package com.example.pomodoroproject.viewmodels

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.pomodoroproject.navigation.LOGIN_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.pomodoroproject.models.User
import com.example.pomodoroproject.navigation.DASHBOARD_URL

class AuthRepository(var navHostController: NavHostController, var context: Context) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val progress: ProgressDialog = ProgressDialog(context).apply {
        setTitle("Loading...")
        setMessage("Please wait")
    }

    fun register(username: String, email: String, password: String) {
        progress.show()
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progress.dismiss()
            if (task.isSuccessful) {
                val uid = mAuth.currentUser?.uid
                if (uid != null) {
                    val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    val userData = User(username, email, password, uid)
                    databaseRef.setValue(userData)
                    Toast.makeText(context, "Account creation successful!!", Toast.LENGTH_SHORT).show()
                    logOut()
                } else {
                    Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Account creation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email: String, password: String) {
            progress.show()
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progress.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                navHostController.navigate(DASHBOARD_URL) // Replace with your defined destination
            } else {
                Toast.makeText(context, task.exception?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return mAuth.currentUser != null
    }

    fun logOut() {
        mAuth.signOut()
        navHostController.navigate(LOGIN_URL)
    }

    fun getUserId(): String {
        return mAuth.currentUser?.uid ?: ""
    }




}
