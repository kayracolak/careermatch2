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
    const val TRANSCRIPT = "transcript" // Yeni Rota
    const val HOME = "home"
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

        // 1. LOGIN EKRANI
        // Başarılı giriş yaparsa direkt Bölüm Seçme ekranına gider.
        composable(Routes.LOGIN) {
            LoginScreen(
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT) },
                onLoginSuccess = {
                    navController.navigate(Routes.DEPARTMENT) {
                        // Geri tuşuna basınca tekrar Login'e dönmemesi için Login'i stack'ten siliyoruz
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 2. REGISTER EKRANI
        // Başarılı kayıt olursa direkt Bölüm Seçme ekranına gider.
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DEPARTMENT) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // 3. FORGOT PASSWORD EKRANI
        composable(Routes.FORGOT) {
            ForgotPasswordScreen(
                onPasswordReset = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // 4. DEPARTMENT SELECTION (Bölüm Seçimi)
        // Bölüm seçildikten sonra Transkript ekranına yönlendirir.
        composable(Routes.DEPARTMENT) {
            DepartmentSelectionScreen(
                onSelectionSuccess = {
                    // Bölüm seçimi tamamlandı, sıradaki adım: Transkript Yükleme
                    navController.navigate(Routes.TRANSCRIPT)
                }
            )
        }

        // 5. TRANSCRIPT EKRANI (Yeni Eklenen Kısım)
        // Transkript yüklenince veya "Mevcutla Devam Et" denilince Ana Sayfaya gider.
        composable(Routes.TRANSCRIPT) {
            TranscriptScreen(
                onNavigateNext = {
                    // İşlemler bitti, Ana Sayfaya (HOME) git.
                    navController.navigate(Routes.HOME) {
                        // Geri tuşuna basınca tekrar transkript veya bölüm seçmeye dönmesin diye temizliyoruz
                        popUpTo(Routes.DEPARTMENT) { inclusive = true }
                    }
                },
                onLogout = {
                    // Sağ üstteki çıkış butonuna basılırsa Login'e at ve her şeyi temizle
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        // 6. HOME SCREEN (Ana Sayfa)
        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}