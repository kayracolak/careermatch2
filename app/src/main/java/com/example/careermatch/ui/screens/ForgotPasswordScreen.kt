package com.example.careermatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onPasswordReset: () -> Unit,
    onBackToLogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var customError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("Şifremi Unuttum", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                customError = ""
            },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        if (customError.isNotEmpty()) {
            Text(customError, color = MaterialTheme.colorScheme.error)
        }

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {

                if (email.isBlank()) {
                    customError = "Lütfen mail giriniz"
                    return@Button
                }

                if (!email.endsWith(".com")) {
                    customError = "Lütfen geçerli bir mail adresi giriniz"
                    return@Button
                }

                vm.resetPassword(email) {
                    onPasswordReset()
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Şifre Sıfırlama Maili Gönder")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = { onBackToLogin() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Geri Dön (Giriş Ekranı)")
        }
    }
}
