package com.example.careermatch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.careermatch.ui.screens.*

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot_password"
    const val DEPARTMENT = "department_select"
    const val TRANSCRIPT = "transcript"
    const val HOME = "home"
    const val JOB_SEARCH = "job_search"
    const val SAVED_JOBS = "saved_jobs"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {

        // 1. LOGIN
        composable(Routes.LOGIN) {

            val authViewModel: com.example.careermatch.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            LoginScreen(
                vm = authViewModel,
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT) },
                onLoginSuccess = {

                    authViewModel.checkUserStatus { hasTranscript ->
                        if (hasTranscript) {
                            navController.navigate(Routes.TRANSCRIPT) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        } else {

                            navController.navigate(Routes.DEPARTMENT) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // 2. REGISTER EKRAN
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DEPARTMENT) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // 3. FORGOT PASSWORD EKRAN
        composable(Routes.FORGOT) {
            ForgotPasswordScreen(
                onPasswordReset = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // 4. DEPARTMENT SELECTION
        composable(Routes.DEPARTMENT) {
            DepartmentSelectionScreen(
                onSelectionSuccess = {
                    // Bölüm seçimi tamamlandı, sıradaki adım: Transkript Yükleme
                    navController.navigate(Routes.TRANSCRIPT)
                }
            )
        }

        // 5. TRANSCRIPT EKRAN
        composable(Routes.TRANSCRIPT) {
            TranscriptScreen(
                onNavigateNext = {
                    navController.navigate(Routes.HOME) {

                        popUpTo(Routes.DEPARTMENT) { inclusive = true }
                    }
                },
                onLogout = {

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        // 6. HOME SCREEN
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                onLogout = {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        }

        // 7. İŞ ARAMA EKRAN
        composable(Routes.JOB_SEARCH) {
            JobSearchScreen(
                navController = navController,
                onLogout = {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        }

        // 8. KAYDEDİLEN İŞLER
        composable(Routes.SAVED_JOBS) {
            SavedJobsScreen(
                navController = navController
            )
        }
    }
}