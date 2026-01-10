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
        composable(Routes.LOGIN) {
            // ViewModel'i burada çağırıp kontrol edebiliriz
            val authViewModel: com.example.careermatch.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            LoginScreen(
                vm = authViewModel, // ViewModel'i içeri paslıyoruz
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT) },
                onLoginSuccess = {
                    // GİRİŞ BAŞARILI OLDUĞUNDA KONTROL ET
                    authViewModel.checkUserStatus { hasTranscript ->
                        if (hasTranscript) {
                            navController.navigate(Routes.TRANSCRIPT) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        } else {
                            // Yoksa -> Bölüm Seçimi (Department)
                            navController.navigate(Routes.DEPARTMENT) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
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
            HomeScreen(
                navController = navController,
                onLogout = {
                    // 1. Firebase'den Çıkış Yap
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

                    // 2. Giriş Ekranına Gönder ve Geçmişi Temizle
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // Tüm ekranları kapat, geri dönülemesin
                    }
                }
            )
        }

        // 7. İŞ ARAMA EKRANI
        composable(Routes.JOB_SEARCH) {
            JobSearchScreen(
                onLogout = {
                    // 1. Firebase'den Oturumu Kapat
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

                    // 2. Giriş Ekranına Yönlendir ve Geçmişi Temizle (Geri dönemesin)
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}