package com.example.careermatch.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.viewmodel.TranscriptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptScreen(
    onNavigateNext: () -> Unit,
    onLogout: () -> Unit,
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
                Toast.makeText(context, "Transkript Başarıyla İşlendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Akademik Profil") },
                actions = {
                    IconButton(onClick = { viewModel.logout { onLogout() } }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Çıkış")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // DURUM 1: Transkript Zaten Var (Kullanıcıyı yormayalım)
            if (existingUrl != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Açık Yeşil
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Transkriptin Hazır!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Sistemde kayıtlı güncel transkriptin bulunuyor. Direkt analize geçebilirsin.",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Büyük Devam Butonu
                Button(
                    onClick = { onNavigateNext() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Analize Başla", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Küçük Güncelle Butonu
                TextButton(onClick = { pdfPickerLauncher.launch("application/pdf") }) {
                    Text("Farklı bir transkript yükle", color = Color.Gray)
                }

            } else {
                // DURUM 2: Transkript Yok (Şık bir yükleme alanı)
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Transkriptini Yükle",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Okul sisteminden aldığın PDF formatındaki not dökümünü buraya ekle. Yapay zeka derslerini analiz edecek.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (loading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Transkript işleniyor...", color = MaterialTheme.colorScheme.primary)
                } else {
                    Button(
                        onClick = { pdfPickerLauncher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PDF Dosyası Seç")
                    }
                }

                // Durum mesajı (Hata veya Başarı)
                if (status != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = status!!,
                        color = if (status!!.contains("Hata")) Color.Red else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    // Eğer yeni yüklendiyse ve başarıysa devam butonu çıkar
                    if (status!!.contains("Başarıyla")) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onNavigateNext() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Devam Et")
                        }
                    }
                }
            }
        }
    }
}