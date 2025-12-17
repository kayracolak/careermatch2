package com.example.careermatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    // ViewModel'den gelen verileri dinliyoruz
    val analysisResult by viewModel.analysisResult.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // Sayfa uzun olursa aşağı kaydırabilelim diye ScrollState ekliyoruz
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kariyer Asistanı") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState), // Kaydırma özelliği
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Üst Bilgilendirme Kartı
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Yüklediğin transkript verilerine göre yapay zeka senin için en uygun kariyer yolunu çizecek.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Analiz Butonu ve Yükleme Durumu
            if (loading) {
                // Yükleniyorsa dönen çember ve yazı
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Transkript inceleniyor...\nGemini AI düşünüyor...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                // Yükleme yoksa butonu göster
                Button(
                    onClick = { viewModel.analyzeUserTranscript() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null) // Star her zaman çalışır
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yapay Zeka ile Analiz Et", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Sonuç Raporu Alanı (Eğer sonuç varsa görünür)
            if (analysisResult != null && !loading) {
                Text(
                    text = "Analiz Raporu",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // Hafif gri arka plan
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = analysisResult!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }

                // Sayfanın en altına biraz boşluk bırakalım
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}