package com.example.careermatch.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.viewmodel.TranscriptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptScreen(
    onNavigateNext: () -> Unit,
    onLogout: () -> Unit, // <-- İşte eksik olan parametreyi buraya ekledik
    viewModel: TranscriptViewModel = viewModel()
) {
    val loading by viewModel.loading.collectAsState()
    val status by viewModel.uploadStatus.collectAsState()
    val existingUrl by viewModel.existingTranscriptUrl.collectAsState()
    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.uploadPdf(context, uri) {
                Toast.makeText(context, "Transkript Eklendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Scaffold kullanarak üst bar (TopBar) ekliyoruz
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transkript Yükle") },
                actions = {
                    // Çıkış Yap Butonu
                    IconButton(onClick = {
                        // ViewModel üzerinden çıkış işlemini yapabilirsin veya direkt onLogout çağırabilirsin
                        // Şimdilik direkt onLogout çağırıyoruz, AuthViewModel'den de tetiklenebilir ama basit tutalım.
                        viewModel.logout { onLogout() }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Çıkış Yap"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // İçerik kısmı
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Scaffold padding'i
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (existingUrl != null) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Var",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sistemde kayıtlı transkriptin var.", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onNavigateNext() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Mevcut Transkriptimle Devam Et")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("veya yenisini yükle:", color = Color.Gray)
            } else {
                Text(
                    "Ders eşleşmesi için okul sisteminden aldığın PDF dosyasını yükle.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (loading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("İşleniyor...")
            } else {
                OutlinedButton(
                    onClick = { pdfPickerLauncher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(if (existingUrl != null) "Farklı Bir PDF Yükle" else "PDF Dosyası Seç")
                }
            }

            if (status != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = status!!,
                    color = if (status!!.contains("Başarısız") || status!!.contains("Hata")) Color.Red else Color.Black
                )

                if (existingUrl == null && status!!.contains("Başarıyla")) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onNavigateNext() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Devam Et")
                    }
                }
            }
        }
    }
}