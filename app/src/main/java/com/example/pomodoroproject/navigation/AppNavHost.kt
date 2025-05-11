package com.example.pomodoroproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroproject.viewmodels.SessionViewModel
import com.example.pomodoroproject.screens.DashboardScreen
import com.example.pomodoroproject.screens.LoginScreen
import com.example.pomodoroproject.screens.RegisterScreen
import com.example.pomodoroproject.screens.SessionInfoScreen
import com.example.pomodoroproject.screens.SessionScreen
import com.example.pomodoroproject.screens.ViewSessionsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import com.example.pomodoroproject.viewmodels.SessionViewModelFactory

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    startDestination: String = "login"
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(navHostController)
        }
        composable("register") {
            RegisterScreen(navHostController)
        }
        composable(
            "start_session/{sessionId}/{sessionName}/{userId}/{pomo}/{short}/{long}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("sessionName") { type = NavType.StringType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("pomo") { type = NavType.IntType },
                navArgument("short") { type = NavType.IntType },
                navArgument("long") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val sessionName = backStackEntry.arguments?.getString("sessionName") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val pomo = backStackEntry.arguments?.getInt("pomo") ?: 25
            val short = backStackEntry.arguments?.getInt("short") ?: 5
            val long = backStackEntry.arguments?.getInt("long") ?: 15

            val context = LocalContext.current
            val application = context.applicationContext as Application
            val viewModel: SessionViewModel = viewModel(
                factory = SessionViewModelFactory(application)
            )



            SessionScreen(
                navHostController = navHostController,
                sessionId = sessionId,
                sessionName = sessionName,
                userId = userId,
                pomo = pomo,
                short = short,
                long = long,
                viewModel = viewModel
            )
        }
        composable("view_sessions") {
            ViewSessionsScreen(navHostController)
        }
        composable("dashboard") {
            DashboardScreen(navHostController)
        }
        composable("session_info/{sessionId}") {
            val sessionId = "1" //Change this later
            //backStackEntry -> val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            SessionInfoScreen(sessionId = sessionId, navHostController = navHostController)
        }

    }
}
