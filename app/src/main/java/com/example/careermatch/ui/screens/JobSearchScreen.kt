package com.example.careermatch.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.model.JobPosting
import com.example.careermatch.viewmodel.JobSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSearchScreen(
    viewModel: JobSearchViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val jobs by viewModel.jobs.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val analysisResult by viewModel.analysisResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    var titleQuery by remember { mutableStateOf("Software Engineer") }
    var locationQuery by remember { mutableStateOf("Turkey") }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(analysisResult) {
        if (analysisResult != null) {
            showBottomSheet = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("İş İlanı Ara & Analiz Et") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Çıkış Yap",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ARAMA ALANI
            OutlinedTextField(
                value = titleQuery,
                onValueChange = { titleQuery = it },
                label = { Text("Pozisyon (Örn: Android Dev)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = locationQuery,
                onValueChange = { locationQuery = it },
                label = { Text("Konum (Örn: Istanbul)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.searchJobs(titleQuery, locationQuery) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("İş İlanlarını Ara")
                }
            }

            if (error != null) {
                Text(text = error!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LİSTE ALANI
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(jobs) { job ->
                    JobCard(
                        job = job,
                        onApplyClick = {
                            val url = job.jobUrl ?: job.jobUrl
                            if (!url.isNullOrEmpty()) {
                                // 1. DÜZELTME: Uri.parse yerine .toUri() kullanıldı
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            }
                        },
                        onAnalyzeClick = {
                            val desc = job.descriptionText ?: "Bu ilanın detaylı açıklaması yok."
                            viewModel.analyzeJobCompatibility(desc)
                        }
                        // 3. DÜZELTME: isAnalyzingThisJob parametresi gereksizdi, sildik.
                    )
                }
            }
        }

        // BOTTOM SHEET (ANALİZ SONUCU)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.clearAnalysis()
                },
                sheetState = sheetState
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text("Yapay Zeka Analiz Raporu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                    // 2. DÜZELTME: Divider yerine HorizontalDivider kullanıldı
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    if (analysisResult != null) {
                        Text(analysisResult!!, style = MaterialTheme.typography.bodyMedium)
                    } else {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        if (isAnalyzing && !showBottomSheet) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Transkriptin işle kıyaslanıyor...")
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(
    job: JobPosting,
    onApplyClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = job.companyName ?: "Gizli Şirket", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = job.location ?: "Remote", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                OutlinedButton(
                    onClick = onApplyClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("İlana Git")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Analiz Et")
                }
            }
        }
    }
}